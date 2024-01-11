package com.koddy.server.global.config.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("cors")
public record CorsProperties(
        Set<String> allowedOriginPatterns
) {
}
