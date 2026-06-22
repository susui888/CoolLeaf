package org.coollib.leaf.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import ch.qos.logback.core.AppenderBase
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Timestamp
import java.sql.Types
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class PostgresLogAppender : AppenderBase<ILoggingEvent>() {

    // Connection credentials passed from LogbackInitializer
    var url: String? = null
    var username: String? = null
    var password: String? = null

    private var dataSource: HikariDataSource? = null
    private val queue = LinkedBlockingQueue<ILoggingEvent>(10000) // In-memory ring buffer
    private var flusherThread: Thread? = null
    @Volatile private var running = true

    override fun start() {
        if (url.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            addError("PostgresLogAppender start failed: Missing database connection parameters!")
            return
        }

        // 1. Initialize an independent Hikari pool dedicated to async logging to isolate business transactions
        try {
            val config = HikariConfig().apply {
                jdbcUrl = url
                username = this@PostgresLogAppender.username
                password = this@PostgresLogAppender.password
                driverClassName = "org.postgresql.Driver"
                maximumPoolSize = 2 // Keeping it lightweight; 2 connections are sufficient for bulk logging
                minimumIdle = 1
                poolName = "LogbackPostgresPool"
            }
            dataSource = HikariDataSource(config)
        } catch (e: Exception) {
            addError("Logback database connection pool initialization failed", e)
            return
        }

        super.start()

        // 2. Spin up a background daemon thread to poll telemetry events and execute batch inserts
        flusherThread = Thread { flushLoop() }.apply {
            name = "postgres-log-flusher"
            isDaemon = true
            start()
        }
    }

    override fun append(eventObject: ILoggingEvent?) {
        // Triggered every time the system logs a message
        if (eventObject == null || !running) return

        // Core Engineering Practice: Non-blocking. Drop logs if the queue fills up (e.g., DB down)
        // to prevent jamming or degrading the hot path of the core business thread.
        queue.offer(eventObject)
    }

    private fun flushLoop() {
        val batch = ArrayList<ILoggingEvent>()
        while (running) {
            try {
                // Execute a batch write every 1.5 seconds, or as soon as 100 logs are gathered
                val element = queue.poll(1500, TimeUnit.MILLISECONDS)
                if (element != null) {
                    batch.add(element)
                }

                if (batch.isNotEmpty() && (batch.size >= 100 || element == null)) {
                    writeBatchToPostgres(batch)
                    batch.clear()
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            } catch (e: Exception) {
                addError("Failed to execute asynchronous batch write to Postgres", e)
            }
        }
    }

    private fun writeBatchToPostgres(events: List<ILoggingEvent>) {
        val sql = """
            INSERT INTO telemetry.app_logs (environment, platform, level, trace_id, tag, message, stack_trace, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        dataSource?.connection?.use { conn ->
            conn.prepareStatement(sql).use { ps ->
                for (event in events) {
                    ps.setString(1, "local")

                    // Automatically categorize telemetry source based on the logger package signature
                    val isClient = event.loggerName.contains("client", ignoreCase = true)
                    ps.setString(2, if (isClient) "mobile" else "backend")

                    ps.setString(3, event.level.toString())

                    // Extract the cross-platform correlation ID injected by TraceIdFilter
                    ps.setString(4, event.mdcPropertyMap["trace_id"])

                    ps.setString(5, event.loggerName)
                    ps.setString(6, event.formattedMessage)

                    // Serialize full stack traces for ERROR logs into the large text field
                    val throwableProxy: IThrowableProxy? = event.throwableProxy
                    if (throwableProxy != null) {
                        ps.setString(7, ThrowableProxyUtil.asString(throwableProxy))
                    } else {
                        ps.setNull(7, Types.CLOB)
                    }

                    ps.setTimestamp(8, Timestamp(event.timeStamp))
                    ps.addBatch()
                }
                ps.executeBatch() // Execute batch updates to minimize physical database I/O overhead
            }
        }
    }

    override fun stop() {
        running = false
        flusherThread?.interrupt()
        dataSource?.close()
        super.stop()
    }
}