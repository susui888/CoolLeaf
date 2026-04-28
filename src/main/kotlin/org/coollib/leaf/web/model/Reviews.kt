package org.coollib.leaf.web.model

import java.time.Instant


data class Review(
    val id: Int? = null,
    val bookId: Int,
    val userId: Int,
    val userName: String,
    val rating: Short,
    val content: String?,
    val createdAt: Instant
)

