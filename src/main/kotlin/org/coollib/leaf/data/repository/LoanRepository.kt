package org.coollib.leaf.data.repository

import org.coollib.leaf.data.entity.LoanEntity
import org.coollib.leaf.data.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoanRepository : JpaRepository<LoanEntity, Long> {
    fun findByUserOrderByIdDesc(user: UserEntity): List<LoanEntity>
}