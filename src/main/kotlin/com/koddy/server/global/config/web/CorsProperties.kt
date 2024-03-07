package com.koddy.server.global.config.web

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
    val allowedOriginPatterns: Set<String> = emptySet(),
)
/**
 * Failed to bind properties (Kotlin data class during test as no setter found)
 * https://github.com/spring-projects/spring-boot/issues/33969
 * https://github.com/spring-projects/spring-boot/issues/38556
 */
