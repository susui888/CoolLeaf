package org.coollib.leaf.data.mapper

import org.coollib.leaf.data.entity.BookEntity
import org.coollib.leaf.web.model.Book

fun Book.toBookEntity() = BookEntity(
    id = this.id,
    isbn = this.isbn,
    title = this.title,
    author = this.author,
    publisher = this.publisher,
    year = this.year
)

fun BookEntity.toBook() = Book(
    this.id,
    this.isbn,
    this.title,
    this.author,
    this.publisher,
    this.year,
    this.availablecopies > 1,
)

fun BookEntity.toBookDetail() = Book(
    this.id,
    this.isbn,
    this.title,
    this.author,
    this.publisher,
    this.year,
    this.availablecopies > 1,
    this.description
)