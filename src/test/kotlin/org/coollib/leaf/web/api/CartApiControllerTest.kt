package org.coollib.leaf.web.api

import org.coollib.leaf.config.JwtUtils
import org.coollib.leaf.service.LoanService
import org.coollib.leaf.service.UserService
import org.coollib.leaf.web.model.Cart
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import tools.jackson.databind.ObjectMapper

/**
 * Test for CartApiController borrowing logic.
 * Handles JSON serialization and Result type folding.
 */
@WebMvcTest(CartApiController::class)
class CartApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper // Used to convert objects to JSON strings

    @MockitoBean
    lateinit var loanService: LoanService

    @MockitoBean
    private lateinit var jwtUtils: JwtUtils

    @MockitoBean
    private lateinit var userService: UserService

    @Test
    fun `borrowBooks - should return 200 on success`() {
        // GIVEN
        val username = "test_user"
        val cartItems = listOf(Cart(bookId = 101), Cart(bookId = 102))

        // Mock successful result from service
        whenever(loanService.borrowBooks(eq(username), any())).thenReturn(Result.success(Unit))

        // WHEN
        mockMvc.perform(
            post("/api/cart/borrow")
                .with(user(username)) // Mock UserDetails with this username
                .with(csrf())         // Spring Security usually requires CSRF for POST in tests
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItems))
        )
            // THEN
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.message").value("Loan processing completed for user: $username"))
    }

    @Test
    fun `borrowBooks - should return 500 on failure`() {
        // GIVEN
        val username = "test_user"
        val errorMessage = "Book not available"

        // Mock failure result from service
        whenever(loanService.borrowBooks(eq(username), any()))
            .thenReturn(Result.failure(RuntimeException(errorMessage)))

        // WHEN
        mockMvc.perform(
            post("/api/cart/borrow")
                .with(user(username))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(listOf(Cart(1))))
        )
            // THEN
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.message").value(errorMessage))
    }
}