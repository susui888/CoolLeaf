package org.coollib.leaf.web.api

import org.coollib.leaf.service.ReviewService
import org.coollib.leaf.web.model.Review
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@RestController
@RequestMapping("/api/reviews")
class ReviewController(private val reviewService: ReviewService) {

    @GetMapping("/{id}")
    fun getReviewsByBookId(@PathVariable id: Int) =
        reviewService.getReviewsByBookId(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReview(@RequestBody request: CreateReviewRequest): Review {
        return reviewService.createReview(
            bookId = request.bookId,
            userId = request.userId,
            rating = request.rating,
            content = request.content,
        )
    }

}

data class CreateReviewRequest(
    val bookId: Int,
    val userId: Int,
    val rating: Int,
    val content: String
)