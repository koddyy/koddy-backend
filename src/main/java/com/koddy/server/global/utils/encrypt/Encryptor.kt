package com.koddy.server.global.utils.encrypt

interface Encryptor {
    fun hash(value: String): String

    fun matches(
        rawValue: String,
        encodedValue: String,
    ): Boolean

    fun encrypt(value: String): String

    fun decrypt(value: String): String
}
