package com.koddy.server.global.config.encrypt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "encrypt")
data class EncryptorProperties(
    val secretKey: String,
    val salt: String,
)
