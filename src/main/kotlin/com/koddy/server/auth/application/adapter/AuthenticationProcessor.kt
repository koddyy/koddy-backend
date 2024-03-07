package com.koddy.server.auth.application.adapter

interface AuthenticationProcessor {
    fun storeAuthCode(key: String): String

    fun verifyAuthCode(
        key: String,
        value: String,
    )

    fun deleteAuthCode(key: String)
}
