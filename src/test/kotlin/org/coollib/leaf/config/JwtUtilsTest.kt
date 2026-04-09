package org.coollib.leaf.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UserDetails

class JwtUtilsTest {

    private lateinit var jwtUtils: JwtUtils
    private val secret = "a-very-long-and-secure-secret-key-for-test-12345"
    private val expiration: Long = 3600000 // 1 hour

    @BeforeEach
    fun setup() {
        // Inject configuration directly via constructor, no Spring container needed
        jwtUtils = JwtUtils(secret, expiration)
    }

    @Test
    fun `should generate and extract correct username`() {
        // Arrange
        val username = "test_user"

        // Act
        val token = jwtUtils.generateToken(username)
        val extracted = jwtUtils.extractUsername(token)

        // Assert
        assertNotNull(token)
        assertEquals(username, extracted)
    }

    @Test
    fun `validateToken should return true for valid token`() {
        // Arrange
        val username = "valid_user"
        val token = jwtUtils.generateToken(username)
        val userDetails: UserDetails = mock()
        whenever(userDetails.username).thenReturn(username)

        // Act
        val isValid = jwtUtils.validateToken(token, userDetails)

        // Assert
        assertTrue(isValid)
    }

    @Test
    fun `validateToken should return false for wrong username`() {
        // Arrange
        val token = jwtUtils.generateToken("user_a")
        val userDetails: UserDetails = mock()
        whenever(userDetails.username).thenReturn("user_b") // Username does not match

        // Act
        val isValid = jwtUtils.validateToken(token, userDetails)

        // Assert
        assertFalse(isValid)
    }

    @Test
    fun `should return null when extracting username from invalid token`() {
        // Act
        val extracted = jwtUtils.extractUsername("invalid.token.string")

        // Assert
        assertNull(extracted)
    }

    @Test
    fun `should return false for expired token`() {
        // Arrange
        // Create an already expired JwtUtils instance (negative expiration offset)
        val expiredUtils = JwtUtils(secret, -1000)
        val token = expiredUtils.generateToken("expired_user")
        val userDetails: UserDetails = mock()
        whenever(userDetails.username).thenReturn("expired_user")

        // Act
        val isValid = jwtUtils.validateToken(token, userDetails)

        // Assert
        assertFalse(isValid)
    }

    @Test
    fun `should fail if token is tampered`() {
        // Arrange
        val token = jwtUtils.generateToken("original_user")
        val tamperedToken = token + "modified" // Tamper with the token string

        // Act
        val extracted = jwtUtils.extractUsername(tamperedToken)

        // Assert
        assertNull(extracted, "Tampered token should result in null claims")
    }
}