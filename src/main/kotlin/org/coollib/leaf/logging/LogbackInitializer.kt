package org.coollib.leaf.logging

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener

class LogbackInitializer : ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    override fun onApplicationEvent(event: ApplicationEnvironmentPreparedEvent) {
        val environment = event.environment

        // 🎯 修复这里：先获取 ILoggerFactory，再安全的强转为 Logback 的 LoggerContext
        val loggerFactory = LoggerFactory.getILoggerFactory()
        if (loggerFactory !is LoggerContext) {
            return // 如果当前日志系统不是 Logback，直接跳过，防止强转崩溃
        }
        val loggerContext: LoggerContext = loggerFactory

        // 2. 动态创建我们手写的 Appender
        val postgresAppender = PostgresLogAppender().apply {
            context = loggerContext
            name = "POSTGRES_DYNAMIC"

            // 3. 从 Spring 环境中读取变量
            url = "jdbc:postgresql://${environment.getProperty("DB_HOST", "192.168.2.18")}:${environment.getProperty("DB_PORT", "5433")}/${environment.getProperty("POSTGRES_DB", "postgres")}"
            username = environment.getProperty("POSTGRES_USER", "postgres")
            password = environment.getProperty("POSTGRES_PASSWORD", "postgres")
        }

        // 4. 手动触发 start 建立 Hikari 连接池
        postgresAppender.start()

        // 5. 将它追加到 Logback 的 Root Logger 中
        val rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME)
        rootLogger.addAppender(postgresAppender)
    }
}