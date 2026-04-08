package org.coollib.leaf.config

import jakarta.servlet.http.HttpServletResponse
import org.coollib.leaf.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    /**
     * Define the password encoder bean.
     * BCrypt is the current recommended standard for password hashing.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    /**
     * Configure the AuthenticationProvider.
     * It uses the custom UserService to load user data and the passwordEncoder to verify credentials.
     */
    @Bean
    fun authenticationProvider(userService: UserService): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider(userService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    /**
     * Define the security filter chain.
     * This is the core configuration for HTTP security.
     */
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityFilterChain {
        return http
            // 1. Disable CSRF as we are using JWT (Stateless)
            .csrf { it.disable() }

            // 2. Disable Form Login as we use a custom REST API for authentication
            .formLogin { it.disable() }

            // 3. Configure Session Management to Stateless
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            // 4. Configure Endpoint Permissions
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    "/api/auth/**",
                    "/api/books/**",
                    "/api/category/**",
                    "/api/books/isbn/**",
                    "/img/**",
                    "/isbn/**"
                ).permitAll()
                    .anyRequest().authenticated()
            }

            // 5. Add JWT Filter before the standard Username/Password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

            // 6. Handle Unauthorized exceptions
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { _, response, _ ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                }
            }

            // 7. Enable Logout support
            .logout { it.permitAll() }

            .build()
    }
}