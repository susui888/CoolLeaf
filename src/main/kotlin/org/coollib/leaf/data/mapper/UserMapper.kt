package org.coollib.leaf.data.mapper

import org.coollib.leaf.data.entity.UserEntity
import org.coollib.leaf.web.model.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

fun UserEntity.toUser() = User(
    id = this.id,
    username = this.username,
    password = this.password,
    firstname = this.firstname,
    lastname = this.lastname
)

fun User.toUserEntity() = UserEntity(
    username = this.username,
    password = BCryptPasswordEncoder().encode(this.password),
    firstname = this.firstname,
    lastname = this.lastname
)