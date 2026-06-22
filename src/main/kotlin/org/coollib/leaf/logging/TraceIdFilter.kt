package org.coollib.leaf.logging

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TraceIdFilter : Filter {

    companion object {
        private const val TRACE_ID_HEADER = "X-Trace-Id"
        private const val MDC_TRACE_ID_KEY = "trace_id"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest && response is HttpServletResponse) {

            // 1. 尝试获取客户端的 Trace-Id，没有则自动生成 UUID
            val traceId = request.getHeader(TRACE_ID_HEADER) ?: UUID.randomUUID().toString()

            // 2. 塞入 MDC
            MDC.put(MDC_TRACE_ID_KEY, traceId)

            // 3. 回传给客户端
            response.addHeader(TRACE_ID_HEADER, traceId)
        }

        try {
            // 4. 放行，此时后面的 CommonsRequestLoggingFilter 执行时，MDC 已经有值了！
            chain.doFilter(request, response)
        } finally {
            // 5. 请求彻底结束后清理线程上下文
            MDC.remove(MDC_TRACE_ID_KEY)
        }
    }
}