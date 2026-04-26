package org.coollib.leaf.service

import org.coollib.leaf.data.entity.ReviewEntity
import org.coollib.leaf.data.mapper.toDomain
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.data.repository.ReviewRepository
import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.web.model.Review
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository,
    ) {

    fun getReviewsByBookId(bookId: Int): List<Review> =
        reviewRepository
            .findByBookId(bookId)
            .map { it.toDomain() }

    @Transactional
    fun createReview(bookId: Int, userId: Int, rating: Int, content: String): Review {
        val existingReview = reviewRepository.findByUserIdAndBookId(userId, bookId)

        val reviewToSave = existingReview?.apply {
            this.rating = rating.toShort()
            this.content = content
            this.createdat = Instant.now()
        } ?: ReviewEntity(
            book = bookRepository.getReferenceById(bookId),
            user = userRepository.getReferenceById(userId),
            rating = rating.toShort(),
            content = content,
            createdat = Instant.now(),
        )

        return reviewRepository.save(reviewToSave).toDomain()
    }
}
