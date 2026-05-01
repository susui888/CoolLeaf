package org.coollib.leaf.data.mapper

import jakarta.persistence.EntityManager
import org.coollib.leaf.data.entity.ReviewEntity
import org.coollib.leaf.data.entity.ReviewImageEntity
import org.coollib.leaf.web.model.ReviewImage
import java.time.Instant

fun ReviewImageEntity.toDomain(): ReviewImage {
    return ReviewImage(
        id = this.id,
        reviewId = this.reviewid ?: throw IllegalStateException("Review ID is required"),
        imageUrl = this.imageUrl ?: "",
        width = this.width ?: 0,
        height = this.height ?: 0,
        sortOrder = this.sortOrder ?: 0,
        createdAt = this.createdat ?: Instant.now()
    )
}

fun ReviewImage.toEntity(): ReviewImageEntity {
    val entity = ReviewImageEntity()
    entity.id = this.id
    entity.reviewid = this.reviewId
    entity.imageUrl = this.imageUrl
    entity.width = this.width
    entity.height = this.height
    entity.sortOrder = this.sortOrder
    entity.createdat = this.createdAt

    return entity
}