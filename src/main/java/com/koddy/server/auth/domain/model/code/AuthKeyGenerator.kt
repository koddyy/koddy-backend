package com.koddy.server.auth.domain.model.code

fun interface AuthKeyGenerator {
    fun get(
        prefix: String,
        vararg suffix: Any,
    ): String
}
