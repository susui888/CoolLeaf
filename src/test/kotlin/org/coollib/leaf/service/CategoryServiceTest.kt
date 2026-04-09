package org.coollib.leaf.service

import org.coollib.leaf.data.mapper.toEntity
import org.coollib.leaf.data.repository.CategoryRepository
import org.coollib.leaf.mock.MockCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.util.*

class CategoryServiceTest {

    private val categoryRepository: CategoryRepository = mock()
    private val categoryService = CategoryService(categoryRepository)

    @Test
    fun `getAllCategory should return mapped categories from repository`() {
        // Arrange
        val mockModels = MockCategory.newList()
        val mockEntities = mockModels.map { it.toEntity() }

        whenever(categoryRepository.findAll()).thenReturn(mockEntities)

        // Act
        val result = categoryService.getAllCategory()

        // Assert
        assertEquals(mockModels.size, result.size)
        assertEquals(mockModels[0].name, result[0].name)
        assertEquals(mockModels[1].id, result[1].id)

        verify(categoryRepository).findAll()
    }

    @Test
    fun `getCategoryById should return category when id exists`() {
        // Arrange
        val mockModel = MockCategory.newList().first() // Fiction (ID: 266)
        val mockEntity = mockModel.toEntity()

        whenever(categoryRepository.findById(266)).thenReturn(Optional.of(mockEntity))

        // Act
        val result = categoryService.getCategoryById(266)

        // Assert
        assertEquals("Fiction", result.name)
        assertEquals(266, result.id)
        verify(categoryRepository).findById(266)
    }

    @Test
    fun `getCategoryById should throw NoSuchElementException when not found`() {
        // Arrange
        val targetId = 999
        whenever(categoryRepository.findById(targetId)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows<NoSuchElementException> {
            categoryService.getCategoryById(targetId)
        }

        assertEquals("Category with id $targetId not found", exception.message)
        verify(categoryRepository).findById(targetId)
    }
}