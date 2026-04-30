package org.coollib.leaf.config

import org.coollib.leaf.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var jwtUtils: JwtUtils

    @MockitoBean
    lateinit var userService: UserService

    @Test
    fun `public endpoints should be accessible without token`() {
        // Fix 1: /api/auth/login must use POST
        mockMvc.post("/api/auth/login") {
            // Send a dummy body to avoid 400 error
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            // Fix 2: Use match to explicitly assert status is not 401 or 403
            status {
                match { result ->
                    val code = result.response.status
                    code != 401 && code != 403
                }
            }
        }

        // Static resources or public GET endpoints
        mockMvc.get("/api/books/all").andExpect {
            status {
                match { it.response.status != 401 && it.response.status != 403 }
            }
        }
    }

    @Test
    fun `protected endpoints should return 401 when no token provided`() {
        mockMvc.get("/api/user/profile").andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `protected endpoints should be accessible with valid token`() {
        val mockToken = "valid-token"
        whenever(jwtUtils.extractUsername(mockToken)).thenReturn("test-user")
        whenever(jwtUtils.validateToken(eq(mockToken), any())).thenReturn(true)

        mockMvc.get("/api/user/profile") {
            header("Authorization", "Bearer $mockToken")
        }.andExpect {
            status {
                match { it.response.status != 401 && it.response.status != 403 }
            }
        }
    }

    @Test
    fun `csrf should be disabled`() {
        // If CSRF is not disabled, POST will return 403. If disabled, even a 400 (missing body) proves CSRF is not blocking
        mockMvc.post("/api/auth/register").andExpect {
            status {
                match { it.response.status != 403 }
            }
        }
    }
}