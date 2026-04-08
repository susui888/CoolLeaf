package org.coollib.leaf.service

import org.coollib.leaf.data.mapper.toUser
import org.coollib.leaf.data.mapper.toUserEntity
import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.web.model.User
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

    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByUsername(username)?.toUser()
            ?: throw UsernameNotFoundException("User not found $username")

    fun login(username: String, password: String): Result<UserDetails> {

        val userEntity = userRepository.findByUsername(username)
            ?: return Result.failure(UsernameNotFoundException("User not found"))

        if (!passwordEncoder.matches(password, userEntity.password)) {
            return Result.failure(IllegalArgumentException("Invalid password"))
        }

        return Result.success(userEntity.toUser())
    }

    fun register(user: User): Result<Unit> {

        if (userRepository.findByUsername(user.username) != null) {
            return Result.failure(
                DuplicateKeyException("Username '${user.username}' already exists!")
            )
        }

        val userEntity = user.toUserEntity().copy(
            password = passwordEncoder.encode(user.password)
        )

        userRepository.save(userEntity)

        return Result.success(Unit)
    }
}