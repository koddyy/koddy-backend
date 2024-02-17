package com.koddy.server.global.config.etc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class P6SpyConfig {
    @Bean
    fun p6SpyCustomEventListener(): P6SpyEventListener {
        return P6SpyEventListener()
    }

    @Bean
    fun p6SpyCustomFormatter(): P6SpyFormatter {
        return P6SpyFormatter()
    }
}
