package org.coollib.leaf.web.api

import org.coollib.leaf.service.LoanService
import org.coollib.leaf.web.model.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/loan")
class LoanApiController(
    private val loanService: LoanService
) {
    @GetMapping()
    fun getAllLoans(@AuthenticationPrincipal user: User) =
        loanService.getAllLoans(user.id)
}