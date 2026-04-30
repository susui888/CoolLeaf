package org.coollib.leaf.config

import org.coollib.leaf.data.entity.UserEntity
import org.coollib.leaf.data.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Integration tests for JWT Authentication and Security Filter Chain.
 * Uses MockMvc to simulate HTTP requests without starting a full server.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jwtUtils: JwtUtils

    private val testUsername = "test_user"

    @Autowired
    lateinit var userRepository: UserRepository // Inject your repository

    @BeforeEach
    fun setup() {
        // Clear and seed the test database before each test
        userRepository.deleteAll()
        val testUser = UserEntity(username = "test_user", password = "hashed_password")
        userRepository.save(testUser)
    }

    /**
     * Test: Accessing a protected endpoint with a valid JWT should return 200 OK.
     */
    @Test
    fun `should allow access with valid token`() {
        // 1. Generate a valid token for a mock user
        val token = jwtUtils.generateToken(testUsername)

        // 2. Perform GET request to a protected endpoint (e.g., /api/loan)
        mockMvc.get("/api/loan") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
        }
    }

    /**
     * Test: Accessing a protected endpoint without any token should return 401 Unauthorized.
     */
    @Test
    fun `should deny access when token is missing`() {
        mockMvc.get("/api/loan")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    /**
     * Test: Accessing with an invalid/tampered token should return 401 Unauthorized.
     */
    @Test
    fun `should deny access with tampered token`() {
        val validToken = jwtUtils.generateToken(testUsername)
        // Tamper with the signature by adding an extra character
        val tamperedToken = "z$validToken"

        mockMvc.get("/api/loan") {
            header("Authorization", "Bearer $tamperedToken")
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    /**
     * Test: Public endpoints defined in SecurityConfig should be accessible without a token.
     */
    /**
     * Test: Public endpoints should be accessible to anyone.
     */
    @Test
    fun `should allow access to public endpoints`() {
        // Use isOk() or isNotFound() depending on if the route actually exists.
        // The key is that it should NOT be 401.
        mockMvc.get("/api/books/newest")
            .andExpect {
                status { isOk() }
            }
    }
}