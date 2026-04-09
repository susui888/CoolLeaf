package org.coollib.leaf.service

import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.web.model.User
import org.coollib.leaf.data.entity.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest {

    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = mock()

    private val userService = UserService(userRepository, passwordEncoder)

    @Test
    fun `loadUserByUsername should return UserDetails when user exists`() {
        // Arrange
        val username = "testuser"
        val userEntity = UserEntity(username = username, password = "hashed_password", email = "test@ex.com")
        whenever(userRepository.findByUsername(username)).thenReturn(userEntity)

        // Act
        val result = userService.loadUserByUsername(username)

        // Assert
        assertEquals(username, result.username)
        verify(userRepository).findByUsername(username)
    }

    @Test
    fun `loadUserByUsername should throw exception when user not found`() {
        // Arrange
        whenever(userRepository.findByUsername(anyString())).thenReturn(null)

        // Act & Assert
        assertThrows<UsernameNotFoundException> {
            userService.loadUserByUsername("nonexistent")
        }
    }

    @Test
    fun `login should return success when credentials are valid`() {
        // Arrange
        val username = "testuser"
        val rawPassword = "password123"
        val hashedEmail = "hashed_password"
        val userEntity = UserEntity(username = username, password = hashedEmail, email = "a@b.com")

        whenever(userRepository.findByUsername(username)).thenReturn(userEntity)
        whenever(passwordEncoder.matches(rawPassword, hashedEmail)).thenReturn(true)

        // Act
        val result = userService.login(username, rawPassword)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(username, result.getOrNull()?.username)
    }

    @Test
    fun `login should return failure when password matches false`() {
        // Arrange
        val userEntity = UserEntity(username = "user", password = "hashed", email = "a@b.com")
        whenever(userRepository.findByUsername(any())).thenReturn(userEntity)
        whenever(passwordEncoder.matches(any(), any())).thenReturn(false)

        // Act
        val result = userService.login("user", "wrong_pass")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Invalid password", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register should save encoded password and return success`() {
        // Arrange
        val newUser = User(username = "newbie", password = "raw_password", email = "new@ex.com")
        val encodedPassword = "encoded_password_123"

        whenever(userRepository.findByUsername(newUser.username)).thenReturn(null)
        whenever(passwordEncoder.encode(newUser.password)).thenReturn(encodedPassword)

        // Act
        val result = userService.register(newUser)

        // Assert
        assertTrue(result.isSuccess)

        // 验证是否保存了加密后的密码
        verify(userRepository).save(check {
            assertEquals(newUser.username, it.username)
            assertEquals(encodedPassword, it.password)
        })
    }

    @Test
    fun `register should return failure when username already exists`() {
        // Arrange
        val newUser = User(username = "exists", password = "p", email = "e")
        val existingEntity = UserEntity(username = "exists", password = "p", email = "e")

        whenever(userRepository.findByUsername("exists")).thenReturn(existingEntity)

        // Act
        val result = userService.register(newUser)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DuplicateKeyException)
        verify(userRepository, never()).save(any())
    }
}