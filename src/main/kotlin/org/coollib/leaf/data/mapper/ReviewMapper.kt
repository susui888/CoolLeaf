package org.coollib.leaf.data.mapper


import jakarta.persistence.EntityManager
import org.coollib.leaf.data.entity.BookEntity
import org.coollib.leaf.data.entity.ReviewEntity
import org.coollib.leaf.data.entity.UserEntity
import org.coollib.leaf.web.model.Review
import java.time.Instant

fun ReviewEntity.toDomain(): Review {
    return Review(
        id = this.id,

        bookId = this.book?.id ?: throw IllegalStateException("Book ID is required"),
        userId = this.user?.id ?: throw IllegalStateException("User ID is required"),
        userName = this.user?.username ?: throw IllegalStateException("User name is required"),
        rating = this.rating ?: 0,
        content = this.content,
        createdAt = this.createdat ?: Instant.now(),
        imageUrls = this.images?.sortedBy { it.sortOrder }?.map { it.imageUrl ?: "" } ?: emptyList(),
    )
}

fun Review.toEntity(entityManager: EntityManager): ReviewEntity {
    val entity = ReviewEntity()
    entity.id = this.id

    entity.book = entityManager.getReference(BookEntity::class.java, this.bookId)
    entity.user = entityManager.getReference(UserEntity::class.java, this.userId)
    entity.rating = this.rating
    entity.content = this.content

    return entity
}