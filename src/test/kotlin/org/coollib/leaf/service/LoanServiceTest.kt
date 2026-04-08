package org.coollib.leaf.service

import org.coollib.leaf.data.entity.LoanEntity
import org.coollib.leaf.data.entity.UserEntity
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.data.repository.LoanRepository
import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.mock.MockBooks
import org.coollib.leaf.web.model.Cart
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class LoanServiceTest {

    @Mock
    lateinit var loanRepository: LoanRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var bookRepository: BookRepository

    @InjectMocks
    lateinit var loanService: LoanService

    // --- borrowBooks Tests ---

    @Test
    fun `borrowBooks - successful loan processing for multiple books`() {
        // GIVEN: Set up user and mock books from your MockBooks object
        val username = "clean_coder_99"
        val user = UserEntity(id = 1, username = username)
        val mockBooks = MockBooks.newList()
        val book1 = mockBooks[0] // Sapiens
        val book2 = mockBooks[1] // Clean Code

        whenever(userRepository.findByUsername(username)).thenReturn(user)
        whenever(bookRepository.findById(101)).thenReturn(Optional.of(book1))
        whenever(bookRepository.findById(102)).thenReturn(Optional.of(book2))

        // Use thenAnswer to return the saved entity (mimics real DB behavior)
        whenever(loanRepository.save(any<LoanEntity>())).thenAnswer { it.arguments[0] as LoanEntity }

        // WHEN: Executing the borrow logic
        val cartItems = listOf(Cart(bookId = 101), Cart(bookId = 102))
        val result = loanService.borrowBooks(username, cartItems)

        // THEN: Verify success and repository interactions
        assertTrue(result.isSuccess)
        verify(userRepository).findByUsername(username)
        verify(bookRepository, times(2)).findById(any())
        verify(loanRepository, times(2)).save(any())
    }

    @Test
    fun `borrowBooks - returns failure when user is not found`() {
        // GIVEN: User doesn't exist in the system
        val username = "ghost_user"
        whenever(userRepository.findByUsername(username)).thenReturn(null)

        // WHEN
        val result = loanService.borrowBooks(username, listOf(Cart(bookId = 1)))

        // THEN: Result should capture the NoSuchElementException
        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is NoSuchElementException)
        assertTrue(error?.message?.contains("User $username not found") == true)

        // Ensure no database writes were attempted
        verify(loanRepository, never()).save(any())
    }

    @Test
    fun `borrowBooks - returns failure when a book ID does not exist`() {
        // GIVEN: User exists but one book in the cart is invalid
        val username = "active_reader"
        val user = UserEntity(id = 2, username = username)
        whenever(userRepository.findByUsername(username)).thenReturn(user)
        whenever(bookRepository.findById(999)).thenReturn(Optional.empty())

        // WHEN
        val result = loanService.borrowBooks(username, listOf(Cart(bookId = 999)))

        // THEN
        assertTrue(result.isFailure)
        assertEquals("Book ID 999 not found.", result.exceptionOrNull()?.message)
    }

    // --- getAllLoans Tests ---

    @Test
    fun `getAllLoans - returns loans mapped to web model for valid user`() {
        // GIVEN: Prepare mock data
        val userId = 42
        val user = UserEntity(id = userId, username = "test_user")
        val mockBook = MockBooks.newList().first()

        val loanEntities = listOf(
            LoanEntity(
                id = 1,
                user = user,
                book = mockBook,
                borrowdate = LocalDate.now(),
                duedate = LocalDate.now().plusDays(14)
            )
        )

        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(loanRepository.findByUserOrderByIdDesc(user)).thenReturn(loanEntities)

        // WHEN
        val loans = loanService.getAllLoans(userId)

        // THEN
        assertNotNull(loans)
        assertEquals(1, loans.size)
        verify(userRepository).findById(userId)
        verify(loanRepository).findByUserOrderByIdDesc(user)
    }

    @Test
    fun `getAllLoans - throws NoSuchElementException for missing user`() {
        // GIVEN
        whenever(userRepository.findById(any())).thenReturn(Optional.empty())

        // WHEN & THEN: Verify immediate exception (method is not wrapped in Result)
        assertThrows(NoSuchElementException::class.java) {
            loanService.getAllLoans(500)
        }
    }
}