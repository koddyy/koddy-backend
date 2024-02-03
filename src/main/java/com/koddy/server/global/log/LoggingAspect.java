package com.koddy.server.global.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final LoggingTracer loggingTracer;

    @Pointcut("execution(* com.koddy.server..*(..))")
    private void includeComponent() {
    }

    @Pointcut("""
            !execution(* com.koddy.server.global.annotation..*(..))
            && !execution(* com.koddy.server.global.aop..*(..))
            && !execution(* com.koddy.server.global.base..*(..))
            && !execution(* com.koddy.server.global.config..*(..))
            && !execution(* com.koddy.server.global.decorator..*(..))
            && !execution(* com.koddy.server.global.filter..*(..))
            && !execution(* com.koddy.server.global.log..*(..))
            && !execution(* com.koddy.server..*HealthCheckApiController.*(..))
            && !execution(* com.koddy.server..*AnonymousRequestExceptionHandler.*(..))
            && !execution(* com.koddy.server..*Config.*(..))
            && !execution(* com.koddy.server..*Formatter.*(..))
            && !execution(* com.koddy.server..*Properties.*(..))
            && !execution(* com.koddy.server..*TokenProvider.*(..))
            && !execution(* com.koddy.server..*TokenResponseWriter.*(..))
            && !execution(* com.koddy.server..*TokenExtractor.*(..))
            """)
    private void excludeComponent() {
    }

    @Around("includeComponent() && excludeComponent()")
    public Object doLogging(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodSignature = joinPoint.getSignature().toShortString();
        final Object[] args = joinPoint.getArgs();
        loggingTracer.methodCall(methodSignature, args);
        try {
            final Object result = joinPoint.proceed();
            loggingTracer.methodReturn(methodSignature);
            return result;
        } catch (final Throwable e) {
            loggingTracer.throwException(methodSignature, e);
            throw e;
        }
    }
}
