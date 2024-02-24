package com.koddy.server.common.config

import com.koddy.server.auth.utils.TokenResponseWriter
import com.koddy.server.global.config.web.CorsProperties
import com.koddy.server.global.exception.notify.ExceptionNotifier
import com.koddy.server.global.log.logger
import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestWebBeanConfig {
    private val log: Logger = logger()

    @Bean
    fun corsProperties(): CorsProperties = CorsProperties(setOf("http://localhost:8080"))

    @Bean
    fun tokenResponseWriter(): TokenResponseWriter = TokenResponseWriter(1234)

    @Bean
    fun exceptionNotifier(): ExceptionNotifier = ExceptionNotifier { request, exception -> log.error("에러 발생 -> $request $exception") }
}
