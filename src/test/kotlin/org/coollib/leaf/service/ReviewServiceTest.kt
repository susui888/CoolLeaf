package org.coollib.leaf.service

import org.coollib.leaf.data.entity.BookEntity
import org.coollib.leaf.data.entity.ReviewEntity
import org.coollib.leaf.data.entity.ReviewImageEntity
import org.coollib.leaf.data.entity.UserEntity
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.data.repository.ReviewImageRepository
import org.coollib.leaf.data.repository.ReviewRepository
import org.coollib.leaf.data.repository.UserRepository
import org.coollib.leaf.web.model.Review
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class ReviewServiceTest {

    @Mock
    private lateinit var reviewRepository: ReviewRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var reviewImageRepository: ReviewImageRepository

    private lateinit var reviewService: ReviewService

    @BeforeEach
    fun setUp() {
        reviewService = ReviewService(reviewRepository, userRepository, bookRepository, reviewImageRepository)
    }

    @Test
    fun `getReviewsByBookId should return a list of reviews`() {
        // Given
        val bookId = 1
        val reviewEntity1 = ReviewEntity().apply {
            id = 1
            book = BookEntity(id = bookId, isbn = "isbn1", title = "Book1", author = "Author1")
            user = UserEntity(id = 1, username = "user1")
            rating = 5.toShort()
            content = "Great book!"
            createdat = Instant.now()
            images = mutableListOf()
        }
        val reviewEntity2 = ReviewEntity().apply {
            id = 2
            book = BookEntity(id = bookId, isbn = "isbn2", title = "Book2", author = "Author2")
            user = UserEntity(id = 2, username = "user2")
            rating = 4.toShort()
            content = "Good read."
            createdat = Instant.now()
            images = mutableListOf()
        }
        val mockReviewEntities = listOf(reviewEntity1, reviewEntity2)

        `when`(reviewRepository.findByBookId(bookId)).thenReturn(mockReviewEntities)

        // When
        val result: List<Review> = reviewService.getReviewsByBookId(bookId)

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(reviewEntity1.id, result[0].id)
        assertEquals(reviewEntity1.content, result[0].content)
        assertEquals(reviewEntity2.id, result[1].id)
        assertEquals(reviewEntity2.content, result[1].content)

        verify(reviewRepository, times(1)).findByBookId(bookId)
        verifyNoMoreInteractions(reviewRepository)
    }

    @Test
    fun `createReview should create a new review with images`() {
        // Given
        val bookId = 1
        val userId = 1
        val rating = 5
        val content = "Excellent book!"
        val imageUrls = listOf("http://image.com/img1.jpg", "http://image.com/img2.jpg")

        val userEntity = UserEntity(id = userId, username = "testuser")
        val bookEntity = BookEntity(id = bookId, isbn = "isbn3", title = "Test Book", author = "Test Author")
        val savedReviewEntity = ReviewEntity().apply {
            id = 100
            book = bookEntity
            user = userEntity
            this.rating = rating.toShort()
            this.content = content
            createdat = Instant.now()
            // Populate images list for the returned entity
            images = imageUrls.mapIndexed { index, url ->
                ReviewImageEntity().apply {
                    id = index + 1 // Assign a mock ID
                    reviewid = this@apply.id
                    imageUrl = url
                    width = 1080
                    height = 1920
                    sortOrder = index.toShort()
                    createdat = Instant.now()
                }
            }.toMutableList()
        }

        `when`(reviewRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(null) // No existing review
        `when`(userRepository.getReferenceById(userId)).thenReturn(userEntity)
        `when`(bookRepository.getReferenceById(bookId)).thenReturn(bookEntity)
        `when`(reviewRepository.save(any(ReviewEntity::class.java))).thenReturn(savedReviewEntity)
        `when`(reviewRepository.findById(savedReviewEntity.id!!.toLong())).thenReturn(Optional.of(savedReviewEntity))

        // When
        val result: Review = reviewService.createReview(bookId, userId, rating, content, imageUrls)

        // Then
        assertNotNull(result)
        assertEquals(savedReviewEntity.id, result.id)
        assertEquals(content, result.content)
        assertEquals(rating.toShort(), result.rating)
        assertEquals(2, result.imageUrls.size) // This assertion should now pass

        verify(reviewRepository, times(1)).findByUserIdAndBookId(userId, bookId)
        verify(userRepository, times(1)).getReferenceById(userId)
        verify(bookRepository, times(1)).getReferenceById(bookId)
        verify(reviewRepository, times(1)).save(any(ReviewEntity::class.java))
        verify(reviewImageRepository, times(1)).deleteByReviewid(savedReviewEntity.id!!)
        verify(reviewImageRepository, times(1)).saveAll(anyList())
        verify(reviewRepository, times(1)).findById(savedReviewEntity.id!!.toLong())
        verifyNoMoreInteractions(reviewRepository, userRepository, bookRepository, reviewImageRepository)
    }

    @Test
    fun `createReview should update an existing review and its images`() {
        // Given
        val bookId = 1
        val userId = 1
        val oldRating = 3
        val oldContent = "Old content."
        val newRating = 5
        val newContent = "Updated content!"
        val imageUrls = listOf("http://image.com/new_img.jpg")

        val existingReviewEntity = ReviewEntity().apply {
            id = 100
            book = BookEntity(id = bookId, isbn = "isbn4", title = "Book4", author = "Author4")
            user = UserEntity(id = userId, username = "testuser")
            rating = oldRating.toShort()
            content = oldContent
            createdat = Instant.now().minusSeconds(3600)
            images = mutableListOf(ReviewImageEntity().apply {
                id = 1
                reviewid = 100
                imageUrl = "http://image.com/old_img.jpg"
                width = 100
                height = 200
                sortOrder = 0
                createdat = Instant.now()
            })
        }
        val updatedReviewEntity = ReviewEntity().apply {
            id = existingReviewEntity.id
            book = existingReviewEntity.book
            user = existingReviewEntity.user
            rating = newRating.toShort()
            content = newContent
            createdat = Instant.now() // Will be updated by the service
            // Populate images list for the returned entity
            images = imageUrls.mapIndexed { index, url ->
                ReviewImageEntity().apply {
                    id = index + 101 // Assign a mock ID, different from existing
                    reviewid = this@apply.id
                    imageUrl = url
                    width = 1080
                    height = 1920
                    sortOrder = index.toShort()
                    createdat = Instant.now()
                }
            }.toMutableList()
        }

        `when`(reviewRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(existingReviewEntity)
        `when`(reviewRepository.save(any(ReviewEntity::class.java))).thenReturn(updatedReviewEntity)
        `when`(reviewRepository.findById(existingReviewEntity.id!!.toLong())).thenReturn(Optional.of(updatedReviewEntity))

        // When
        val result: Review = reviewService.createReview(bookId, userId, newRating, newContent, imageUrls)

        // Then
        assertNotNull(result)
        assertEquals(existingReviewEntity.id, result.id)
        assertEquals(newContent, result.content)
        assertEquals(newRating.toShort(), result.rating)
        assertEquals(1, result.imageUrls.size) // This assertion should now pass
        assertEquals(imageUrls[0], result.imageUrls[0])

        verify(reviewRepository, times(1)).findByUserIdAndBookId(userId, bookId)
        verify(reviewRepository, times(1)).save(any(ReviewEntity::class.java))
        verify(reviewImageRepository, times(1)).deleteByReviewid(existingReviewEntity.id!!)
        verify(reviewImageRepository, times(1)).saveAll(anyList())
        verify(reviewRepository, times(1)).findById(existingReviewEntity.id!!.toLong())
        verifyNoMoreInteractions(reviewRepository, userRepository, bookRepository, reviewImageRepository)
    }
}
