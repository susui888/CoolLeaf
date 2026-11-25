package org.coollib.leaf.service

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.coollib.leaf.data.repository.BookRepository
import org.coollib.leaf.data.repository.LoanRepository
import org.coollib.leaf.data.repository.ReviewImageRepository
import org.coollib.leaf.data.repository.ReviewRepository
import org.coollib.leaf.data.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class StatsService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val loanRepository: LoanRepository,
    private val reviewImageRepository: ReviewImageRepository,
    private val meterRegistry: MeterRegistry,
) {
    private val suppliers = mutableListOf<() -> Long>()

    init {
        registerEntityGauge("books") { bookRepository.count() }
        registerEntityGauge("users") { userRepository.count() }
        registerEntityGauge("loans") { loanRepository.count() }
        registerEntityGauge("reviews") { reviewRepository.count() }
        registerEntityGauge("review_image") { reviewImageRepository.count() }
    }

    private fun registerEntityGauge(
        entityName: String,
        supplier: () -> Long,
    ) {
        suppliers += supplier
        Gauge
            .builder("coolleaf_entities", supplier) { it().toDouble() }
            .description("Total number of records per entity")
            .tag("entity", entityName)
            .register(meterRegistry)
    }

    fun getCounts(): Map<String, Long> =
        mapOf(
            "books" to bookRepository.count(),
            "users" to userRepository.count(),
            "loans" to loanRepository.count(),
            "reviews" to reviewRepository.count(),
            "reviewImage" to reviewImageRepository.count(),
        )
}