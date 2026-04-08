package org.coollib.leaf.web.api

import org.coollib.leaf.service.LoanService
import org.coollib.leaf.web.model.Cart
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
class CartApiController(
    private val loanService: LoanService
) {

    @PostMapping("/borrow")
    fun borrowBooks(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody carts: List<Cart>
    ): ResponseEntity<Map<String, Any>> {

        val username = userDetails.username

        val result = loanService.borrowBooks(username, carts)

        return result.fold(
            onSuccess = {
                ResponseEntity.ok(
                    mapOf(
                        "status" to "success",
                        "message" to "Loan processing completed for user: $username"
                    )
                )
            },
            onFailure = { error ->
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    mapOf(
                        "status" to "error",
                        "message" to (error.message ?: "Failed to process book loan")
                    )
                )
            }
        )
    }
}