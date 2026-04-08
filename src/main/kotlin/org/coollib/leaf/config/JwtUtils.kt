package org.coollib.leaf.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

/**
 * Utility class for generating, parsing, and validating JSON Web Tokens (JWT).
 */
@Component
class JwtUtils(
    @Value($$"${jwt.secret:YourSuperSecretKeyForJwtAuthentication1234567890}")
    private val secretString: String,

    @Value($$"${jwt.expiration:2592000000}") // Default 30 days
    private val expirationTimeMillis: Long
) {

    // Generate a secure signing key from the secret string
    private val signingKey: SecretKey = Keys.hmacShaKeyFor(secretString.toByteArray())

    /**
     * Generate a new JWT for a specific username.
     */
    fun generateToken(username: String): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date(now))
            .expiration(Date(now + expirationTimeMillis))
            .signWith(signingKey)
            .compact()
    }

    /**
     * Extract the username (subject) from the token.
     */
    fun extractUsername(token: String): String? =
        extractAllClaims(token)?.subject

    /**
     * Validate if the token belongs to the user and is not expired.
     */
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    /**
     * Check if the token's expiration date is in the past.
     */
    private fun isTokenExpired(token: String): Boolean {
        val expiration = extractAllClaims(token)?.expiration
        return expiration?.before(Date()) ?: true
    }

    /**
     * Parse and verify the token signature to extract claims.
     * Returns null if the token is invalid, tampered with, or expired.
     */
    private fun extractAllClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            // Log exception here if necessary for debugging (e.g., ExpiredJwtException)
            null
        }
    }
}