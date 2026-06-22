package org.coollib.leaf.service

import org.coollib.leaf.data.entity.LoanEntity
import org.coollib.leaf.data.mapper.toLoan
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.data.repository.LoanRepository
import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.web.model.Loan
import org.coollib.leaf.web.model.Cart
import org.slf4j.LoggerFactory
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
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun borrowBooks(username: String, carts: List<Cart>): Result<Unit> {
        val defaultLoanPeriodDays = 14L
        val borrowDate = LocalDate.now()
        val dueDate = borrowDate.plus(defaultLoanPeriodDays, ChronoUnit.DAYS)

        log.info("User [{}] is attempting to borrow {} items", username, carts.size)

        return runCatching {
            val user = userRepository.findByUsername(username)
                ?: throw NoSuchElementException("User $username not found.").also {
                    log.warn("Borrow failed: Username [{}] not found", username)
                }

            carts.forEach { cartItem ->
                val book = bookRepository.findById(cartItem.bookId)
                    .orElseThrow {
                        log.warn("Borrow failed: Book ID [{}] not found for user [{}]", cartItem.bookId, username)
                        NoSuchElementException("Book ID ${cartItem.bookId} not found.")
                    }

                loanRepository.save(
                    LoanEntity(
                        book = book,
                        user = user,
                        borrowdate = borrowDate,
                        duedate = dueDate,
                    )
                )
            }
            log.info("User [{}] successfully borrowed {} items", username, carts.size)
        }.onFailure { e ->
            log.warn("Transaction rolled back. Borrow operation failed for user [{}]: {}", username, e.message)
        }
    }

    @Transactional(readOnly = true)
    fun getAllLoans(userId: Int): List<Loan> {
        val user = userRepository.findById(userId)
            .orElseThrow {
                log.warn("Failed to get loans: User ID [{}] not found", userId)
                NoSuchElementException("User $userId not found.")
            }

        return loanRepository.findByUserOrderByIdDesc(user).map { it.toLoan() }
    }
}