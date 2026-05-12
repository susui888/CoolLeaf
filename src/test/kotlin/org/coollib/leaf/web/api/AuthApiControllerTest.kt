package org.coollib.leaf.web.api

import org.coollib.leaf.config.JwtUtils
import org.coollib.leaf.service.UserService
import org.coollib.leaf.web.model.LoginRequest
import org.coollib.leaf.web.model.RegisterRequest
import org.coollib.leaf.web.model.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import tools.jackson.databind.ObjectMapper

class AuthApiControllerTest {

    private lateinit var mockMvc: MockMvc
    private val userService: UserService = mock()
    private val jwtUtils: JwtUtils = mock()
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        val controller = AuthApiController(userService, jwtUtils)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `login should return token when success`() {
        // Arrange
        val loginRequest = LoginRequest("testuser", "password123")
        val user = User(1, "testuser", "password123")
        val generatedToken = "mocked-jwt-token"

        // 使用 mockito-kotlin 的 whenever
        whenever(userService.login("testuser", "password123")).thenReturn(Result.success(user))
        whenever(jwtUtils.generateToken("testuser")).thenReturn(generatedToken)

        // Act & Assert
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { value(generatedToken) }
            jsonPath("$.username") { value("testuser") }
        }

        // 验证交互
        verify(userService).login("testuser", "password123")
        verify(jwtUtils).generateToken("testuser")
    }

    @Test
    fun `login should return bad request when failure`() {
        // Arrange
        val loginRequest = LoginRequest("wronguser", "wrongpass")
        val errorMessage = "Invalid credentials"

        whenever(userService.login(anyString(), anyString()))
            .thenReturn(Result.failure(Exception(errorMessage)))

        // Act & Assert
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isBadRequest() }
            content { string(errorMessage) }
        }
    }

    @Test
    fun `register should return success message`() {
        // Arrange
        val registerRequest = RegisterRequest("newuser", "pass123", "new@example.com")
        val expectedResponse = MessageResponse("User registered successfully")

        whenever(userService.register(any())).thenReturn(Result.success(Unit))

        // Act & Assert
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isOk() }
            // 方案 A: 使用 jsonPath 验证特定字段 (更灵活)
            jsonPath("$.message") { value("User registered successfully") }

            // 方案 B: 验证完整的 JSON 内容
            // content { json(objectMapper.writeValueAsString(expectedResponse)) }
        }

        verify(userService).register(check {
            assert(it.username == "newuser")
            assert(it.email == "new@example.com")
        })
    }

    @Test
    fun `register should return bad request when failure`() {
        // Arrange
        val registerRequest = RegisterRequest("user", "pass", "mail")
        val errorMessage = "Error"
        whenever(userService.register(any())).thenReturn(Result.failure(Exception(errorMessage)))

        // Act & Assert
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isBadRequest() }
            // 同样更新为验证 JSON 字段
            jsonPath("$.message") { value(errorMessage) }
        }
    }
}