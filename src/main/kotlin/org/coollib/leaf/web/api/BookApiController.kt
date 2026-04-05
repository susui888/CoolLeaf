package org.coollib.leaf.web.api

import org.coollib.leaf.service.BookService
import org.coollib.leaf.web.model.Book
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookApiController(private val bookService: BookService) {

    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) category: Int?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) publisher: String?,
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false, defaultValue = "") searchTerm: String
    ): List<Book> = bookService.findBooks(category, author, publisher, year, searchTerm)


    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Int) =
        bookService.getBook(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody book: Book) =
        bookService.addBook(book)

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: Int, @RequestBody book: Book) =
        bookService.addBook(book)

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Int) =
        bookService.deleteBook(id)
}