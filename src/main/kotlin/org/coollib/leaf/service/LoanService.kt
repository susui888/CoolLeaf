package org.coollib.leaf.service

import org.coollib.leaf.data.entity.LoanEntity
import org.coollib.leaf.data.mapper.toLoan
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.data.repository.LoanRepository
import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.web.model.Loan
import org.coollib.leaf.web.model.Cart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@Service
class LoanService(
    private val loanRepository: LoanRepository,
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository
) {
    @Transactional
    fun borrowBooks(username: String, carts: List<Cart>): Result<Unit> {
        val defaultLoanPeriodDays = 14L
        val borrowDate = LocalDate.now()
        val dueDate = borrowDate.plus(defaultLoanPeriodDays, ChronoUnit.DAYS)

        return runCatching {
            val user =
                userRepository.findByUsername(username) ?: throw NoSuchElementException("User $username not found.")

            carts.forEach { cartItem ->

                val book = bookRepository.findById(cartItem.bookId)
                    .orElseThrow { NoSuchElementException("Book ID ${cartItem.bookId} not found.") }

                loanRepository.save(
                    LoanEntity(
                        book = book,
                        user = user,
                        borrowdate = borrowDate,
                        duedate = dueDate,
                    )
                )
            }
        }
    }

    @Transactional(readOnly = true)
    fun getAllLoans(userId: Int): List<Loan> {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User $userId not found.") }

        return loanRepository.findByUserOrderByIdDesc(user).map { it.toLoan() }
    }
}