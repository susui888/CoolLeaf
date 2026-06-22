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

    // 接收从 LogbackInitializer 传过来的连接凭证
    var url: String? = null
    var username: String? = null
    var password: String? = null

    private var dataSource: HikariDataSource? = null
    private val queue = LinkedBlockingQueue<ILoggingEvent>(10000) // 内存缓冲区
    private var flusherThread: Thread? = null
    @Volatile private var running = true

    override fun start() {
        if (url.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            addError("PostgresLogAppender 启动失败: 数据库连接参数缺失！")
            return
        }

        // 1. 初始化独立的 Hikari 线程池，专供日志系统异步写入，绝不占用业务线程池
        try {
            val config = HikariConfig().apply {
                jdbcUrl = url
                username = this@PostgresLogAppender.username
                password = this@PostgresLogAppender.password
                driverClassName = "org.postgresql.Driver"
                maximumPoolSize = 2 // 日志写入 2 个连接足够，保持轻量
                minimumIdle = 1
                poolName = "LogbackPostgresPool"
            }
            dataSource = HikariDataSource(config)
        } catch (e: Exception) {
            addError("Logback 数据库连接池初始化失败", e)
            return
        }

        super.start()

        // 2. 启动后台守护线程，负责定时定量从队列里“刮”数据，然后批量插入 Postgres
        flusherThread = Thread { flushLoop() }.apply {
            name = "postgres-log-flusher"
            isDaemon = true
            start()
        }
    }

    override fun append(eventObject: ILoggingEvent?) {
        // 每当系统打印一条日志，该方法就会被触发
        if (eventObject == null || !running) return

        // 核心工程实践：非阻塞。如果队列满了（比如数据库挂了），直接丢弃该条日志，绝对不能卡住核心业务主线程
        queue.offer(eventObject)
    }

    private fun flushLoop() {
        val batch = ArrayList<ILoggingEvent>()
        while (running) {
            try {
                // 每隔 1.5 秒，或者凑满 100 条日志，就执行一次批量写入
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
                addError("日志批量写入本地 Postgres 失败", e)
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

                    // 智能识别是移动端日志还是后端日志
                    val isClient = event.loggerName.contains("client", ignoreCase = true)
                    ps.setString(2, if (isClient) "mobile" else "backend")

                    ps.setString(3, event.level.toString())

                    // 🌟 核心：从 MDC 中精准提取我们在 TraceIdFilter 中注入的 trace_id
                    ps.setString(4, event.mdcPropertyMap["trace_id"])

                    ps.setString(5, event.loggerName)
                    ps.setString(6, event.formattedMessage)

                    // 如果有 ERROR 级异常堆栈，全面序列化存入大文本字段
                    val throwableProxy: IThrowableProxy? = event.throwableProxy
                    if (throwableProxy != null) {
                        ps.setString(7, ThrowableProxyUtil.asString(throwableProxy))
                    } else {
                        ps.setNull(7, Types.CLOB)
                    }

                    ps.setTimestamp(8, Timestamp(event.timeStamp))
                    ps.addBatch()
                }
                ps.executeBatch() // 批量提交，性能暴增
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