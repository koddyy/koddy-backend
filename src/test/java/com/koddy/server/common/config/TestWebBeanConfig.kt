package com.koddy.server.common.config

import com.koddy.server.auth.utils.TokenResponseWriter
import com.koddy.server.global.config.web.CorsProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestWebBeanConfig {
    @Bean
    fun corsProperties(): CorsProperties {
        return CorsProperties(setOf("http://localhost:8080"))
    }

    @Bean
    fun tokenResponseWriter(): TokenResponseWriter {
        return TokenResponseWriter(1234)
    }
}
