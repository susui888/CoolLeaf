package org.coollib.leaf.service

import org.coollib.leaf.data.entity.CategoryEntity
import org.coollib.leaf.data.mapper.toBook
import org.coollib.leaf.data.mapper.toBookDetail
import org.coollib.leaf.data.mapper.toBookEntity
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.web.model.Book
import org.springframework.stereotype.Service


@Service
class BookService(private val bookRepository: BookRepository) {

    fun findBooks(category: Int?, author: String?, publisher: String?, year: Int?, searchTerm: String): List<Book> {
        val categoryEntity = category?.let { CategoryEntity(it) }

        return bookRepository.searchBooks(categoryEntity, author, publisher, year, searchTerm).map { it.toBook() }
    }

    fun getBook(id: Int): Book =
        bookRepository.findById(id)
            .map { it.toBookDetail() }
            .orElseThrow { NoSuchElementException("Book with id $id not found") }

    fun addBook(book: Book) =
        bookRepository.save(book.toBookEntity()).toBook()

    fun deleteBook(id: Int) =
        bookRepository.deleteById(id)

    fun getAllBooks() =
        bookRepository.findAll().map { it.toBook() }

}