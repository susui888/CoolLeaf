package org.coollib.leaf.data.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "app_logs", schema = "telemetry")
class AppLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, updatable = false)
    var timestamp: Instant = Instant.now()

    @Column(length = 20)
    var environment: String = "local"

    @Column(nullable = false, length = 20)
    lateinit var platform: String

    @Column(nullable = false, length = 10)
    lateinit var level: String

    @Column(name = "trace_id", length = 50)
    var traceId: String? = null

    @Column(columnDefinition = "TEXT")
    var tag: String? = null

    @Column(columnDefinition = "TEXT", nullable = false)
    lateinit var message: String

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    var stackTrace: String? = null
}