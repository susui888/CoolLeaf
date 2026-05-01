package org.coollib.leaf.web.model

import java.time.Instant

data class ReviewImage(
    val id: Int? = null,
    val reviewId: Int,
    val imageUrl: String,
    val width: Int,
    val height: Int,
    val sortOrder: Short = 0,
    val createdAt: Instant = Instant.now()
)