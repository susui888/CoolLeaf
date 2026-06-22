package org.coollib.leaf.web.model

import java.time.Instant

data class LogResponse(
    val id: Long,
    val timestamp: Instant,
    val environment: String,
    val platform: String,
    val level: String,
    val traceId: String?,
    val tag: String?,
    val message: String,
    val stackTrace: String?
)