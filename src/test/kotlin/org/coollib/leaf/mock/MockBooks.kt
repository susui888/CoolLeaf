package org.coollib.leaf.mock

import org.coollib.leaf.data.entity.BookEntity
import org.coollib.leaf.data.entity.CategoryEntity

object MockBooks {
    val category = CategoryEntity(
        id = 266,
        name = "Fiction",
        description = "No description",
        )

    fun newList() = listOf(

        BookEntity(
            isbn = "9780099590088",
            title = "Sapiens: A Brief History of Humankind",
            author = "Yuval Noah Harari",
            publisher = "Vintage",
            year = 2011,
            category = category,
            description = "In \"Sapiens: A Brief History of Humankind,\" Yuval Noah Harari takes us on a breathtaking journey through the entire span of human history.",
        ),

        BookEntity(
            isbn = "9780132350884",
            title = "Clean Code",
            author = "Robert C. Martin",
            publisher = "Prentice Hall",
            year = 2008,
            category = category,
            description = null,
        ),

        BookEntity(
            isbn = "9780812513738",
            title = "The Shadow Rising (The Wheel of Time, Book 4)",
            author = "Robert Jordan",
            publisher = "Tor Books",
            year = 1992,
            category = category,
            description = null,
        ),

        BookEntity(
            isbn = "9780618640195",
            title = "The Lord of the Rings",
            author = "J.R.R. Tolkien",
            publisher = "Houghton Mifflin Harcourt",
            year = 1954,
            category = category,
            description = null,
        ),

        BookEntity(
            isbn = "9780141439518",
            title = "Pride and Prejudice",
            author = "Jane Austen",
            publisher = "Penguin Classics",
            year = 1813,
            category = category,
            description = null,
        ),
    )
}