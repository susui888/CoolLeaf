package org.coollib.leaf.mock

import org.coollib.leaf.web.model.Category


object MockCategory {
    fun newList() = listOf(
        Category(
            id = 266,
            name = "Fiction",
            description = "No description",
        ),
        Category(
            id = 267,
            name = "History",
            description = "No description",
        ),
        Category(
            id = 266,
            name = "Science",
            description = "No description",
        ),
        Category(
            id = 266,
            name = "Art",
            description = "No description",
        )
    )
}