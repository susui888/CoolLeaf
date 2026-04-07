package org.coollib.leaf.data.repository

import org.coollib.leaf.data.entity.BookEntity
import org.coollib.leaf.data.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<BookEntity, Int> {

    @Query("""
        SELECT b FROM BookEntity b 
        WHERE (:category IS NULL OR b.category = :category)
          AND (:author IS NULL OR b.author = :author)
          AND (:publisher IS NULL OR b.publisher = :publisher)
          AND (:year IS NULL OR b.year = :year)
          AND (:searchTerm IS NULL OR b.title ilike %:searchTerm% OR b.author ilike %:searchTerm%)
    """)
    fun searchBooks(
        category: CategoryEntity?,
        author: String?,
        publisher: String?,
        year: Int?,
        searchTerm: String
    ): List<BookEntity>

    @Query("select p from BookEntity p order by p.year desc, p.id desc limit 15")
    fun getNewestBooks(): List<BookEntity>

    fun findByIsbn(isbn: String): BookEntity?
}