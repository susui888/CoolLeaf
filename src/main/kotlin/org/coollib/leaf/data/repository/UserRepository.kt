package org.coollib.leaf.data.repository

import org.coollib.leaf.data.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Int> {
    fun findByUsername(username: String): UserEntity?
}