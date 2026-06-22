package org.coollib.leaf.service

import org.coollib.leaf.data.mapper.toCategory
import org.coollib.leaf.data.repository.CategoryRepository
import org.coollib.leaf.web.model.Category
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getAllCategory() = categoryRepository
        .findAll()
        .map { it.toCategory() }

    fun getCategoryById(id: Int): Category = categoryRepository
        .findById(id)
        .map { it.toCategory() }
        .orElseThrow {
            log.warn("Category not found for ID: [{}]", id)
            NoSuchElementException("Category with id $id not found")
        }
}