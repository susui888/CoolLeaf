package org.coollib.leaf.service

import org.coollib.leaf.data.entity.ReviewEntity
import org.coollib.leaf.data.mapper.toDomain
import org.coollib.leaf.data.mapper.toEntity
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.data.repository.ReviewImageRepository
import org.coollib.leaf.data.repository.ReviewRepository
import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.web.model.Review
import org.coollib.leaf.web.model.ReviewImage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository,
    private val reviewImageRepository: ReviewImageRepository,
    private val uploadService: UploadService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getReviewsByBookId(bookId: Int): List<Review> =
        reviewRepository
            .findByBookId(bookId)
            .map { it.toDomain() }

    @Transactional
    fun createReview(
        bookId: Int,
        userId: Int,
        rating: Int,
        content: String,
        imageUrls: List<String>
    ): Review {
        log.info("User [{}] is upserting review for book ID: [{}]", userId, bookId)

        val existingReview = reviewRepository.findByUserIdAndBookId(userId, bookId)

        val reviewToSave = existingReview?.apply {
            this.rating = rating.toShort()
            this.content = content
            this.createdat = Instant.now()
        } ?: ReviewEntity().apply {
            book = bookRepository.getReferenceById(bookId)
            user = userRepository.getReferenceById(userId)
            this.rating = rating.toShort()
            this.content = content
            createdat = Instant.now()
        }

        val savedReview = reviewRepository.save(reviewToSave)
        val reviewId = savedReview.id!!

        reviewImageRepository.deleteByReviewid(reviewId)

        if (imageUrls.isNotEmpty()) {
            val imageEntities = imageUrls.mapIndexed { index, url ->
                ReviewImage(
                    reviewId = reviewId,
                    imageUrl = url,
                    width = 1080,
                    height = 1920,
                    sortOrder = index.toShort()
                ).toEntity()
            }
            reviewImageRepository.saveAll(imageEntities)
        }

        return reviewRepository.findById(reviewId.toLong()).get().toDomain()
    }

    @Transactional
    fun deleteReview(userId: Int, bookId: Int) {
        val review = reviewRepository.findByUserIdAndBookId(userId, bookId) ?: return

        log.info("User [{}] is deleting review for book ID: [{}]", userId, bookId)

        val keysToDelete = review.images?.mapNotNull { it.imageUrl }?.map { url ->
            url.substringAfter("https://review.ryansu.uk/", url)
        } ?: emptyList()

        reviewRepository.delete(review)

        if (keysToDelete.isNotEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() {
                    try {
                        uploadService.deleteImages(keysToDelete)
                    } catch (e: Exception) {
                        log.warn("Deferred R2 cleanup failed for keys {}: {}", keysToDelete, e.message)
                    }
                }
            })
        }
    }
}