package org.coollib.leaf.logging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class WebLoggingAspect {

    private val log = LoggerFactory.getLogger(WebLoggingAspect::class.java)

    @Pointcut("within(org.coollib.leaf.web.api..*)")
    fun logPointcut() {}

    @Around("logPointcut()")
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        val request = attributes?.request

        log.info("Entering: [${request?.method}] ${request?.requestURI} -> ${joinPoint.signature.toShortString()}")

        val start = System.currentTimeMillis()
        try {
            val result = joinPoint.proceed()
            val executionTime = System.currentTimeMillis() - start

            log.info("Exiting: ${joinPoint.signature.toShortString()} executed in ${executionTime}ms")
            return result
        } catch (e: IllegalArgumentException) {
            log.warn("Illegal argument: ${joinPoint.signature.toShortString()} - Cause: ${e.message}")
            throw e
        }
    }
}