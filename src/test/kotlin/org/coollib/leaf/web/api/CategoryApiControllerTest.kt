package org.coollib.leaf.web.api

import org.coollib.leaf.config.JwtUtils
import org.coollib.leaf.mock.MockCategory
import org.coollib.leaf.service.CategoryService
import org.coollib.leaf.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(CategoryApiController::class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var categoryService: CategoryService

    private val testCategories = MockCategory.newList()

    @MockitoBean
    private lateinit var jwtUtils: JwtUtils

    @MockitoBean
    private lateinit var userService: UserService

    @Test
    @WithMockUser
    fun `getCategory should return all categories`() {
        // Arrange
        `when`(categoryService.getAllCategory()).thenReturn(testCategories)

        // Act & Assert
        mockMvc.perform(get("/api/category"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(testCategories.size))
            .andExpect(jsonPath("$[0].name").value("Fiction"))
            .andExpect(jsonPath("$[1].name").value("History"))
    }

    @Test
    @WithMockUser
    fun `getCategoryById should return specific category`() {
        // Arrange
        val targetCategory = testCategories[0]
        `when`(categoryService.getCategoryById(266)).thenReturn(targetCategory)

        // Act & Assert
        mockMvc.perform(get("/api/category/266"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(266))
            .andExpect(jsonPath("$.name").value("Fiction"))
    }

    @Test
    @WithMockUser
    fun `getCategoryById should return 404 when service throws exception`() {
        // Arrange
        `when`(categoryService.getCategoryById(999))
            .thenThrow(NoSuchElementException("Category with id 999 not found"))

        // Act & Assert
        // 注意：如果你没有配置全局异常处理器 (RestControllerAdvice)，
        // Spring 默认会将 NoSuchElementException 映射为 500。
        // 如果你希望它是 404，建议在 Controller 或 Service 中处理。
        mockMvc.perform(get("/api/category/999"))
            .andExpect(status().isNotFound)
    }
}