package com.koddy.server.global.config.encrypt

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.encrypt.AesBytesEncryptor
import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class EncryptorConfig(
    private val encryptorProperties: EncryptorProperties,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun bytesEncryptor(): BytesEncryptor {
        return AesBytesEncryptor(encryptorProperties.secretKey, encryptorProperties.salt)
    }
}
