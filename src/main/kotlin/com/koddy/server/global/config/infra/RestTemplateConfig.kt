package com.koddy.server.global.config.infra

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class RestTemplateConfig {
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .setReadTimeout(Duration.ofSeconds(5))
            .setConnectTimeout(Duration.ofSeconds(5))
            .build()
    }
}
