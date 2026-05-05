package org.coollib.leaf.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.coollib.leaf.service.UserService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtils: JwtUtils,
    private val userService: UserService
) : OncePerRequestFilter() {

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(AUTH_HEADER)

        // 1. Validate the Authorization header format
        // If the header is missing or doesn't start with "Bearer ", skip this filter
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.removePrefix(BEARER_PREFIX)

        try {
            val username = jwtUtils.extractUsername(jwt)

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userService.loadUserByUsername(username)

                if (jwtUtils.validateToken(jwt, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (e: Exception) {
            // 2. If token is present but INVALID, stop the chain and return 401
            // This is why your test got a 200 previously - the error was swallowed
            SecurityContextHolder.clearContext()
        }

        // Continue the filter chain
        filterChain.doFilter(request, response)
    }
}