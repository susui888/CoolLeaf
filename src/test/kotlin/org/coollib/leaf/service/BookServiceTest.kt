package org.coollib.leaf.service

import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.mock.MockBooks
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.util.*

class BookServiceTest {

    private val bookRepository: BookRepository = mock()
    private val bookService = BookService(bookRepository)

    @Test
    fun `findBooks should return mapped books from mock data`() {
        // Arrange
        val mockEntities = MockBooks.newList()
        val searchTerm = "History"

        whenever(bookRepository.searchBooks(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), eq(searchTerm)))
            .thenReturn(mockEntities)

        // Act
        val result = bookService.findBooks(null, null, null, null, searchTerm)

        // Assert
        assertEquals(mockEntities.size, result.size)
        assertEquals(mockEntities[0].title, result[0].title) // Sapiens
        assertEquals(mockEntities[1].isbn, result[1].isbn)   // Clean Code

        verify(bookRepository).searchBooks(isNull(), isNull(), isNull(), isNull(), eq(searchTerm))
    }

    @Test
    fun `getBook should use mock entity and map correctly`() {
        // Arrange
        val mockEntity = MockBooks.newList().first() // 获取 Sapiens
        val bookId = 1
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(mockEntity))

        // Act
        val result = bookService.getBook(bookId)

        // Assert
        assertEquals(mockEntity.title, result.title)
        assertEquals(mockEntity.author, result.author)
        verify(bookRepository).findById(bookId)
    }

    @Test
    fun `getNewestBooks should return all mock books`() {
        // Arrange
        val mockEntities = MockBooks.newList()
        whenever(bookRepository.getNewestBooks()).thenReturn(mockEntities)

        // Act
        val result = bookService.getNewestBooks()

        // Assert
        assertEquals(5, result.size)
        assertEquals("The Lord of the Rings", result[3].title)
        verify(bookRepository).getNewestBooks()
    }

    @Test
    fun `getBookByIsbn should throw exception if mock not found`() {
        // Arrange
        whenever(bookRepository.findByIsbn(any())).thenReturn(null)

        // Act & Assert
        assertThrows<NoSuchElementException> {
            bookService.getBookByIsbn("0000000000000")
        }
    }
}