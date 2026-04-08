package org.coollib.leaf.web.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Domain model representing a User, implementing Spring Security's UserDetails.
 */
data class User(
    val id: Int = 0,
    private val username: String = "",
    private var password: String? = "",
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = ""
) : UserDetails {

    /**
     * Returns the authorities granted to the user.
     * Hardcoded to "ROLE_USER" for now.
     */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    /**
     * Returns the password used to authenticate the user.
     */
    override fun getPassword(): String? = password

    /**
     * Manual setter for password, useful during registration or password updates.
     */
    fun setPassword(newPassword: String) {
        this.password = newPassword
    }

    /**
     * Returns the username used to authenticate the user.
     */
    override fun getUsername(): String = username

    /**
     * Indicates whether the user's account has expired.
     * @return true if the account is valid (non-expired)
     */
    override fun isAccountNonExpired(): Boolean = true

    /**
     * Indicates whether the user is locked or unlocked.
     * @return true if the user is not locked
     */
    override fun isAccountNonLocked(): Boolean = true

    /**
     * Indicates whether the user's credentials (password) has expired.
     * @return true if the credentials are valid (non-expired)
     */
    override fun isCredentialsNonExpired(): Boolean = true

    /**
     * Indicates whether the user is enabled or disabled.
     * @return true if the user is enabled
     */
    override fun isEnabled(): Boolean = true
}