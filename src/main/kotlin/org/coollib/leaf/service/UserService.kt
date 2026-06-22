package org.coollib.leaf.service

import org.coollib.leaf.data.mapper.toUser
import org.coollib.leaf.data.mapper.toUserEntity
import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.web.model.User
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy

@Service
class UserService(
    private val userRepository: UserRepository,
    @param:Lazy private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByUsername(username)?.toUser()
            ?: run {
                log.warn("Security authentication failed: Username [{}] not found", username)
                throw UsernameNotFoundException("User not found $username")
            }

    fun login(username: String, password: String): Result<UserDetails> {

        val userEntity = userRepository.findByUsername(username)
            ?: run {
                log.warn("Login failed: Username [{}] not found", username)
                return Result.failure(UsernameNotFoundException("User not found"))
            }

        if (!passwordEncoder.matches(password, userEntity.password)) {
            log.warn("Login failed: Invalid password attempt for username [{}]", username)
            return Result.failure(IllegalArgumentException("Invalid password"))
        }

        return Result.success(userEntity.toUser())
    }

    fun register(user: User): Result<Unit> {

        if (userRepository.findByUsername(user.username) != null) {
            log.warn("Registration rejected: Username [{}] already exists", user.username)
            return Result.failure(
                DuplicateKeyException("Username '${user.username}' already exists!")
            )
        }

        val userEntity = user.toUserEntity().copy(
            password = passwordEncoder.encode(user.password)
        )

        userRepository.save(userEntity)
        log.info("Account successfully created for username: [{}]", user.username)

        return Result.success(Unit)
    }
}