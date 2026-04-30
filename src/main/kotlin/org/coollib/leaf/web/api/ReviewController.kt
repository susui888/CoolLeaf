package org.coollib.leaf.web.api

import org.coollib.leaf.service.ReviewService
import org.coollib.leaf.service.UploadService
import org.coollib.leaf.web.model.Review
import org.coollib.leaf.web.model.User
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val reviewService: ReviewService,
    private val uploadService: UploadService,
) {

    @GetMapping("/{id}")
    fun getReviewsByBookId(@PathVariable id: Int) =
        reviewService.getReviewsByBookId(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReview(
        @RequestBody request: CreateReviewRequest,
        @AuthenticationPrincipal user: User
    ): Review {
        return reviewService.createReview(
            bookId = request.bookId,
            userId = user.id,
            rating = request.rating,
            content = request.content,
        )
    }

    @GetMapping("/upload-urls")
    fun getReviewImageUploadUrls(
        @AuthenticationPrincipal user: User,
        @RequestParam fileNames: List<String> // fileNames=1.webp,2.webp
    ): List<UploadUrlResponse> {
        return uploadService.getPresignedUploadUrls(user.id, fileNames)
    }
}

data class CreateReviewRequest(
    val bookId: Int,
    val rating: Int,
    val content: String
)

data class UploadUrlResponse(
    val uploadUrl: String,
    val objectKey: String
)