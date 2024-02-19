package com.koddy.server.global.log

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect(
    private val loggingTracer: LoggingTracer,
) {
    @Pointcut("execution(* com.koddy.server..*(..))")
    private fun includeComponent() {
    }

    @Pointcut(
        """
            !execution(* com.koddy.server.global.annotation..*(..))
            && !execution(* com.koddy.server.global.aop..*(..))
            && !execution(* com.koddy.server.global.base..*(..))
            && !execution(* com.koddy.server.global.config..*(..))
            && !execution(* com.koddy.server.global.decorator..*(..))
            && !execution(* com.koddy.server.global.filter..*(..))
            && !execution(* com.koddy.server.global.log..*(..))
            && !execution(* com.koddy.server..*HealthCheckApi.*(..))
            && !execution(* com.koddy.server..*AnonymousRequestExceptionHandler.*(..))
            && !execution(* com.koddy.server..*Config.*(..))
            && !execution(* com.koddy.server..*Formatter.*(..))
            && !execution(* com.koddy.server..*Properties.*(..))
            && !execution(* com.koddy.server..*TokenProvider.*(..))
            && !execution(* com.koddy.server..*TokenResponseWriter.*(..))
            && !execution(* com.koddy.server..*TokenExtractor.*(..))
        """,
    )
    private fun excludeComponent() {
    }

    @Around("includeComponent() && excludeComponent()")
    fun doLogging(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature: String = joinPoint.signature.toShortString()
        val args: Array<Any?> = joinPoint.args
        loggingTracer.methodCall(methodSignature, args)
        try {
            val result: Any? = joinPoint.proceed()
            loggingTracer.methodReturn(methodSignature)
            return result
        } catch (e: Throwable) {
            loggingTracer.throwException(methodSignature, e)
            throw e
        }
    }
}
