package org.coollib.leaf.web.api

import org.coollib.leaf.config.JwtUtils
import org.coollib.leaf.service.LoanService
import org.coollib.leaf.service.UserService
import org.coollib.leaf.web.model.Loan
import org.coollib.leaf.web.model.User
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

/**
 * Web Layer test for LoanApiController.
 * Uses @WebMvcTest to focus only on the web components and MockMvc to simulate HTTP requests.
 */
@WebMvcTest(LoanApiController::class)
class LoanApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var loanService: LoanService

    @MockitoBean
    private lateinit var jwtUtils: JwtUtils

    @MockitoBean
    private lateinit var userService: UserService

    @Test
    fun `getAllLoans - should return list of loans for authenticated user`() {
        // GIVEN: Prepare mock user and their loans
        val mockUser = User(id = 1, username = "tester")
        val mockLoans = listOf(
            Loan(
                id = 101,
                bookId = 1,
                borrowDate = LocalDate.now(),
                dueDate = LocalDate.now().plusDays(14),
            )
        )

        whenever(loanService.getAllLoans(mockUser.id)).thenReturn(mockLoans)

        // WHEN: Perform GET request with a mocked AuthenticationPrincipal
        mockMvc.perform(
            get("/api/loan")
                .with(user(mockUser)) // Mocks @AuthenticationPrincipal
                .contentType(MediaType.APPLICATION_JSON)
        )
            // THEN: Verify HTTP status and JSON content
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(101))
            .andExpect(jsonPath("$[0].status").value("Borrowed"))
    }

    @Test
    fun `getAllLoans - should return 401 when user is not authenticated`() {
        // WHEN & THEN: Perform request without .with(user(...))
        mockMvc.perform(get("/api/loan"))
            .andExpect(status().isUnauthorized)
    }
}