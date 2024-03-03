package com.koddy.server.global.utils.encrypt

import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Base64

@Component
class DefaultEncryptor(
    private val passwordEncoder: PasswordEncoder,
    private val bytesEncryptor: BytesEncryptor,
) : Encryptor {
    override fun hash(value: String): String {
        return passwordEncoder.encode(value)
    }

    override fun matches(
        rawValue: String,
        encodedValue: String,
    ): Boolean {
        return passwordEncoder.matches(rawValue, encodedValue)
    }

    override fun encrypt(value: String): String {
        val encryptedBytes: ByteArray = bytesEncryptor.encrypt(value.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    override fun decrypt(value: String): String {
        val decryptedBytes: ByteArray = bytesEncryptor.decrypt(Base64.getDecoder().decode(value))
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }
}
