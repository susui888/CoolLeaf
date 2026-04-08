package org.coollib.leaf.web.api

import org.coollib.leaf.config.JwtUtils
import org.coollib.leaf.service.UserService
import org.coollib.leaf.web.model.LoginRequest
import org.coollib.leaf.web.model.RegisterRequest
import org.coollib.leaf.web.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthApiController(
    private val userService: UserService,
    private val jwtUtils: JwtUtils
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {

        val result = userService.login(request.username, request.password)

        return result.fold(
            onSuccess = { user ->

                val token = jwtUtils.generateToken(user.username)

                ResponseEntity.ok(
                    mapOf(
                        "token" to token,
                        "username" to user.username
                    )
                )
            },
            onFailure = {
                ResponseEntity.badRequest().body(it.message)
            }
        )
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {

        val result = userService.register(
            User(
                username = request.username,
                password = request.password,
                email = request.email
            )
        )

        return result.fold(
            onSuccess = {
                ResponseEntity.ok("User registered successfully")
            },
            onFailure = {
                ResponseEntity.badRequest().body(it.message)
            }
        )
    }
}