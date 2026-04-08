package org.coollib.leaf.web.api

import org.coollib.leaf.config.JwtUtils
import org.coollib.leaf.data.mapper.toBook
import org.coollib.leaf.mock.MockBooks
import org.coollib.leaf.service.BookService
import org.coollib.leaf.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper

@WebMvcTest(BookApiController::class)
@AutoConfigureMockMvc(addFilters = false)
class BookApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var bookService: BookService

    @MockitoBean
    private lateinit var jwtUtils: JwtUtils

    @MockitoBean
    private lateinit var userService: UserService

    private val testBooks = MockBooks.newList().map { it.toBook() }

    @Test
    @WithMockUser
    fun `searchBooks should return all mapped books`() {
        whenever(
            bookService.findBooks(
                anyOrNull(), // category
                anyOrNull(), // author
                anyOrNull(), // publisher
                anyOrNull(), // year
                any()        // searchTerm
            )
        ).thenReturn(testBooks)

        mockMvc.perform(
            get("/api/books/search")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(testBooks.size))
            // 验证第一本书 (Sapiens)
            .andExpect(jsonPath("$[0].title").value("Sapiens: A Brief History of Humankind"))
            .andExpect(jsonPath("$[0].isbn").value("9780099590088"))
    }

    @Test
    @WithMockUser
    fun `getBookById should return the correct book model`() {
        val targetBook = testBooks[1] // Clean Code
        `when`(bookService.getBook(2)).thenReturn(targetBook)

        mockMvc.perform(get("/api/books/2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.author").value("Robert C. Martin"))
    }

    @Test
    fun `addBook should process book with available status`() {
        val newBook = testBooks[3] // The Lord of the Rings
        `when`(bookService.addBook(any())).thenReturn(newBook)

        mockMvc.perform(
            post("/api/books")
                .with(csrf())
                .content(objectMapper.writeValueAsString(newBook))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("The Lord of the Rings"))
    }

    @Test
    @WithMockUser
    fun `deleteBook should invoke service`() {
        mockMvc.perform(delete("/api/books/10"))
            .andExpect(status().isOk)

        verify(bookService).deleteBook(10)
    }
}