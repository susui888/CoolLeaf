package org.coollib.leaf.service

import org.coollib.leaf.data.entity.ReviewEntity
import org.coollib.leaf.data.mapper.toDomain
import org.coollib.leaf.data.repository.ReviewRepository
import org.coollib.leaf.web.model.Review
import org.springframework.stereotype.Service

@Service
class ReviewService(private val reviewRepository: ReviewRepository) {

    fun getReviewsByBookId(bookId: Int): List<Review> =
        reviewRepository
            .findByBookId(bookId)
            .map { it.toDomain() }
}
