package org.coollib.leaf.service

import org.coollib.leaf.data.mapper.toCategory
import org.coollib.leaf.data.repository.CategoryRepository
import org.coollib.leaf.web.model.Category
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getAllCategory() = categoryRepository
        .findAll()
        .map { it.toCategory() }

    fun getCategoryById(id: Int): Category = categoryRepository
        .findById(id)
        .map { it.toCategory() }
        .orElseThrow { NoSuchElementException("Category with id $id not found") }
}