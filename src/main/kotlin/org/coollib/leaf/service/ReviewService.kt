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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant


/**
 * Service class for managing book reviews and their lifecycle.
 * Coordinates database persistence for reviews/images and triggers remote storage cleanup.
 */
@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository,
    private val reviewImageRepository: ReviewImageRepository,
    private val uploadService: UploadService,
    ) {

    /**
     * Retrieves all reviews associated with a specific book.
     * @param bookId The ID of the book.
     * @return A list of [Review] domain objects.
     */
    fun getReviewsByBookId(bookId: Int): List<Review> =
        reviewRepository
            .findByBookId(bookId)
            .map { it.toDomain() }

    /**
     * Creates a new review or updates an existing one for a specific user and book.
     * This operation is transactional to ensure data consistency between reviews and images.
     *
     * @param bookId The ID of the book being reviewed.
     * @param userId The ID of the user writing the review.
     * @param rating The numeric rating (1-5).
     * @param content The text content of the review.
     * @param imageUrls A list of image URLs associated with the review.
     * @return The saved [Review] domain object.
     */
    @Transactional
    fun createReview(
        bookId: Int,
        userId: Int,
        rating: Int,
        content: String,
        imageUrls: List<String>
    ): Review {
        // Check if the user has already reviewed this book
        val existingReview = reviewRepository.findByUserIdAndBookId(userId, bookId)

        // Upsert logic: Update existing entity or create a new one
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

        // Sync images: Remove existing image associations before adding new ones
        reviewImageRepository.deleteByReviewid(reviewId)

        // Save new image entities if provided
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

        // Re-fetch to ensure all JPA relationships (like user/book details) are fully populated in the response
        return reviewRepository.findById(reviewId.toLong()).get().toDomain()
    }

    /**
     * Deletes a review and its associated images from both the database and remote storage.
     *
     * To prevent data inconsistency, the physical file deletion from R2 is deferred
     * until the database transaction successfully commits.
     *
     * @param userId The ID of the owner of the review.
     * @param bookId The ID of the book the review belongs to.
     */
    @Transactional
    fun deleteReview(userId: Int, bookId: Int) {
        // Find the review; if not found, exit early (idempotent behavior)
        val review = reviewRepository.findByUserIdAndBookId(userId, bookId) ?: return

        // Extract object keys from full URLs for R2 API compatibility
        val keysToDelete = review.images?.mapNotNull { it.imageUrl }?.map { url ->
            url.substringAfter("https://review.ryansu.uk/", url)
        } ?: emptyList()

        // Delete from database (Cascade handles ReviewImage records if configured in JPA)
        reviewRepository.delete(review)

        // Register a synchronization hook to trigger R2 deletion only AFTER DB commit
        if (keysToDelete.isNotEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() {
                    try {
                        uploadService.deleteImages(keysToDelete)
                    } catch (e: Exception) {
                        // Log failure but don't roll back the DB transaction (since it's already committed)
                        // A background task or manual audit may be required for failed cleanups
                        println("R2 Clean up failed for keys: $keysToDelete, error: ${e.message}")
                    }
                }
            })
        }
    }
}
