package org.coollib.leaf.data.repository

import org.coollib.leaf.data.entity.ReviewImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewImageRepository : JpaRepository<ReviewImageEntity, Int> {
    fun deleteByReviewid(reviewId: Int)
}