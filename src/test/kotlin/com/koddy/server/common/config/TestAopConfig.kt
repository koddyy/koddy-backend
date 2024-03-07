package com.koddy.server.common.config

import com.koddy.server.global.aop.AccessControlAop
import com.koddy.server.global.log.LoggingStatusManager
import com.koddy.server.global.log.LoggingTracer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy

@TestConfiguration
@EnableAspectJAutoProxy
class TestAopConfig {
    @Bean
    fun accessControlAop(): AccessControlAop = AccessControlAop()

    @Bean
    fun loggingStatusManager(): LoggingStatusManager = LoggingStatusManager()

    @Bean
    fun loggingTracer(): LoggingTracer = LoggingTracer(loggingStatusManager())
}
