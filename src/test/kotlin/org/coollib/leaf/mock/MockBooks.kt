package org.coollib.leaf.mock

import org.coollib.leaf.data.entity.BookEntity
import org.coollib.leaf.web.model.Book

object MockBooks {

    fun newList() = listOf(

        BookEntity(
            isbn = "9780099590088",
            title = "Sapiens: A Brief History of Humankind",
            author = "Yuval Noah Harari",
            publisher = "Vintage",
            year = 2011,
            description = "In \"Sapiens: A Brief History of Humankind,\" Yuval Noah Harari takes us on a breathtaking journey through the entire span of human history.",
        ),

        BookEntity(
            isbn = "9780132350884",
            title = "Clean Code",
            author = "Robert C. Martin",
            publisher = "Prentice Hall",
            year = 2008,
            description = null,
        ),

        BookEntity(
            isbn = "9780812513738",
            title = "The Shadow Rising (The Wheel of Time, Book 4)",
            author = "Robert Jordan",
            publisher = "Tor Books",
            year = 1992,
            description = null,
        ),

        BookEntity(
            isbn = "9780618640195",
            title = "The Lord of the Rings",
            author = "J.R.R. Tolkien",
            publisher = "Houghton Mifflin Harcourt",
            year = 1954,
            description = null,
        ),

        BookEntity(
            isbn = "9780141439518",
            title = "Pride and Prejudice",
            author = "Jane Austen",
            publisher = "Penguin Classics",
            year = 1813,
            description = null,
        ),
    )
}