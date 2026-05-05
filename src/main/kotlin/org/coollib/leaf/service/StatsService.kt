package org.coollib.leaf.service

import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.data.repository.LoanRepository
import org.coollib.leaf.data.repository.ReviewImageRepository
import org.coollib.leaf.data.repository.ReviewRepository
import org.coollib.leaf.data.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class StatsService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val loanRepository: LoanRepository,
    private val reviewImageRepository: ReviewImageRepository,
) {
    fun getCounts(): Map<String, Long> {
        return mapOf(
            "books" to bookRepository.count(),
            "users" to userRepository.count(),
            "loans" to loanRepository.count(),
            "reviews" to reviewRepository.count(),
            "reviewImage" to reviewImageRepository.count()
        )
    }
}
