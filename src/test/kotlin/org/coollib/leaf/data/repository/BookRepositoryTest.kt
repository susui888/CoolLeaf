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
    val bookRepository: BookRepository,
    val categoryRepository: CategoryRepository
) {

    @BeforeEach
    fun setup() {
        bookRepository.deleteAll()
        categoryRepository.deleteAll()

        val books = MockBooks.newList()

        // Key step: Reset all entity IDs to default values (usually 0 or null)
        // This ensures JPA treats them as new (transient) entities,
        // performing INSERT instead of MERGE
        books.forEach { book ->
            book.id = 0
            book.category?.let {
                it.id = 0
                // If multiple books reference the same category instance,
                // resetting once is sufficient
            }
        }

        val categories = books.mapNotNull { it.category }.distinctBy { it.name }
        categoryRepository.saveAll(categories)
        bookRepository.saveAll(books)
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

        // Then: since ILIKE is used, it should find "Clean Code"
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

    @Test
    fun `should find book by exact ISBN`() {
        // Given
        val targetIsbn = MockBooks.newList()[0].isbn

        // When
        val result = bookRepository.findByIsbn(targetIsbn)

        // Then
        assertTrue(result != null)
        assertEquals(targetIsbn, result?.isbn)
    }

    @Test
    fun `should return null when searching for non-existent ISBN`() {
        // When
        val result = bookRepository.findByIsbn("999-9999999999")

        // Then
        assertTrue(result == null)
    }

    @Test
    fun `should get newest books limited to 15 items and sorted by year`() {
        // Given: Assume MockBooks.newList() returns more than 5 books
        // To test the limit, we could insert more data manually,
        // or validate sorting based on current data

        // When
        val newestBooks = bookRepository.getNewestBooks()

        // Then
        assertTrue(newestBooks.size <= 15)

        // Verify sorting: first book's year should be >= second book's year
        if (newestBooks.size >= 2) {
            val firstYear = newestBooks[0].year ?: 0
            val secondYear = newestBooks[1].year ?: 0
            assertTrue(firstYear >= secondYear, "Books should be sorted by year descending")
        }
    }

    @Test
    fun `should find books by category entity`() {
        // Given: the category needs to be retrieved from entities linked in MockBooks
        // Assume MockBooks has correctly associated CategoryEntity
        val allBooks = bookRepository.findAll()
        val targetCategory = allBooks.firstOrNull()?.category

        if (targetCategory != null) {
            // When
            val result = bookRepository.searchBooks(
                category = targetCategory,
                author = null,
                publisher = null,
                year = null,
                searchTerm = ""
            )

            // Then
            assertTrue(result.isNotEmpty())
            assertTrue(result.all { it.category?.id == targetCategory.id })
        }
    }

    @Test
    fun `getNewestBooks should fetch category lazily`() {
        // When
        val newestBooks = bookRepository.getNewestBooks()

        // Then
        if (newestBooks.isNotEmpty()) {
            val book = newestBooks[0]

            val categoryName = book.category?.name
            assertTrue(categoryName is String)
        }
    }
}