package org.coollib.leaf.web.model

data class Cart(
    val bookId: Int,
    var quantity: Int = 1
)