package org.coollib.leaf.data.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid", nullable = false)
    var id: Int = 0,

    @Column(name = "username", nullable = false, unique = true)
    var username: String = "",

    @Column(name = "password", nullable = false)
    var password: String? = "",

    @Column(name = "firstname", nullable = true)
    var firstname: String = "",

    @Column(name = "lastname", nullable = true)
    var lastname: String = "",

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "phonenumber", length = 20)
    var phonenumber: String? = null,

    @Column(name = "registrationdate")
    var registrationdate: LocalDate? = null,

    @Column(name = "address", length = Integer.MAX_VALUE)
    var address: String? = null
)