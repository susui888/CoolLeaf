package org.coollib.leaf.web.api

import org.coollib.leaf.service.ReviewService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reviews")
class ReviewController(private val reviewService: ReviewService) {

    @GetMapping("/{id}")
    fun getReviewsByBookId(@PathVariable id: Int) =
        reviewService.getReviewsByBookId(id)
}