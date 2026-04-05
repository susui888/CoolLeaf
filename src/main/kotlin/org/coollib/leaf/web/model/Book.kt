package org.coollib.leaf.web.model

data class Book(
    var id: Int,
    var isbn: String,
    var title: String,
    var author: String,
    var publisher: String? = "Publisher Unavailable",
    var year: Int? = 1900,
    var available: Boolean = true,
    var description: String? = null,
)
