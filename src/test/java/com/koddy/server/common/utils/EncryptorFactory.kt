package com.koddy.server.common.utils

import java.security.SecureRandom

object EncryptorFactory {
    fun main(args: Array<String>) {
        password()
        salt()
    }

    private fun password() {
        val characters = "0123456789abcdef"

        val random = SecureRandom()
        val sb = StringBuilder(64)

        for (i in 0..63) {
            val randomIndex = random.nextInt(characters.length)
            sb.append(characters[randomIndex])
        }

        println("Password = $sb")
    }

    private fun salt() {
        val random = SecureRandom()
        val salt = ByteArray(32)
        random.nextBytes(salt)

        val sb = StringBuilder()
        for (b in salt) {
            sb.append(String.format("%02x", b))
        }
        println("Salt = $sb")
    }
}
