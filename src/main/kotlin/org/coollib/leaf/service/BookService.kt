package org.coollib.leaf.service

import org.coollib.leaf.data.entity.CategoryEntity
import org.coollib.leaf.data.mapper.toBook
import org.coollib.leaf.data.mapper.toBookDetail
import org.coollib.leaf.data.mapper.toBookEntity
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.web.model.Book
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BookService(private val bookRepository: BookRepository) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun findBooks(category: Int?, author: String?, publisher: String?, year: Int?, searchTerm: String): List<Book> {
        val categoryEntity = category?.let { CategoryEntity(it) }
        return bookRepository.searchBooks(categoryEntity, author, publisher, year, searchTerm).map { it.toBook() }
    }

    fun getBook(id: Int): Book {
        return bookRepository.findById(id)
            .map { it.toBookDetail() }
            .orElseThrow {
                log.warn("Book not found for ID: [{}]", id)
                NoSuchElementException("Book with id $id not found")
            }
    }

    fun addBook(book: Book): Book {
        log.info("Adding new book with ISBN: [{}]", book.isbn)
        return bookRepository.save(book.toBookEntity()).toBook()
    }

    fun deleteBook(id: Int) {
        log.info("Deleting book ID: [{}]", id)
        bookRepository.deleteById(id)
    }

    fun getNewestBooks(): List<Book> {
        return bookRepository.getNewestBooks().map { it.toBook() }
    }

    fun getBookByIsbn(isbn: String): Book {
        return bookRepository.findByIsbn(isbn)
            ?.toBookDetail()
            ?: throw NoSuchElementException("Book with isbn $isbn not found").also {
                log.warn("Book not found for ISBN: [{}]", isbn)
            }
    }
}