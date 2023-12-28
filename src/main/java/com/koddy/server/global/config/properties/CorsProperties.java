package com.koddy.server.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("cors")
public record CorsProperties(
        Set<String> allowedOriginPatterns
) {
}
