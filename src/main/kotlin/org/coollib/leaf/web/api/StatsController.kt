package org.coollib.leaf.web.api

import org.coollib.leaf.service.StatsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stats")
class StatsController(
    private val statsService: StatsService
) {

    @GetMapping("/counts")
    fun getCounts(): Map<String, Long> {
        return statsService.getCounts()
    }
}