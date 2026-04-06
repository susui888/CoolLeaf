package org.coollib.leaf.data.repository

import org.coollib.leaf.mock.MockBooks
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest @Autowired constructor(
    val bookRepository: BookRepository
) {

    @BeforeEach
    fun setup() {
        bookRepository.deleteAll()
        bookRepository.saveAll(MockBooks.newList())
    }

    @Test
    fun `should find books by title search term`() {
        val searchTerm = "Sapiens"

        val result = bookRepository.searchBooks(
            category = null,
            author = null,
            publisher = null,
            year = null,
            searchTerm = searchTerm
        )

        assertEquals(1, result.size)
        assertEquals("Sapiens: A Brief History of Humankind", result[0].title)
    }

    @Test
    fun `should find books by author`() {
        // Given: search for author "Robert Jordan"
        val author = "Robert Jordan"

        // When
        val result = bookRepository.searchBooks(
            category = null,
            author = author,
            publisher = null,
            year = null,
            searchTerm = ""
        )

        // Then
        assertTrue(result.any { it.author == author })
        assertEquals(1, result.size)
    }

    @Test
    fun `should return empty list when no match found`() {
        // When
        val result = bookRepository.searchBooks(
            category = null,
            author = "Unknown Author",
            publisher = null,
            year = null,
            searchTerm = "Nonexistent"
        )

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should find books by multiple criteria`() {
        // Given: search for books published by Prentice Hall in 2008
        val result = bookRepository.searchBooks(
            category = null,
            author = null,
            publisher = "Prentice Hall",
            year = 2008,
            searchTerm = ""
        )

        // Then: should match "Clean Code"
        assertEquals(1, result.size)
        assertEquals("Clean Code", result[0].title)
    }

    @Test
    fun `should be case insensitive for search term`() {
        // Given: use lowercase search term "clean code"
        val searchTerm = "clean code"

        // When
        val result = bookRepository.searchBooks(
            category = null,
            author = null,
            publisher = null,
            year = null,
            searchTerm = searchTerm
        )

        // Then: since ilike is used, it should find "Clean Code"
        assertEquals(1, result.size)
        assertTrue(result[0].title.contains("Clean Code", ignoreCase = true))
    }

    @Test
    fun `should return all books when all parameters are null`() {
        // When: all filters are null and search term is empty
        val result = bookRepository.searchBooks(
            category = null,
            author = null,
            publisher = null,
            year = null,
            searchTerm = ""
        )

        // Then: should return all 5 books from MockBooks
        assertEquals(5, result.size)
    }

    @Test
    fun `should find books by partial author name`() {
        // Given: search term matches part of the author's name (Harari)
        val searchTerm = "Harari"

        // When
        val result = bookRepository.searchBooks(
            category = null,
            author = null,
            publisher = null,
            year = null,
            searchTerm = searchTerm
        )

        // Then: should find "Sapiens"
        assertTrue(result.any { it.author.contains("Harari") })
    }
}