package com.koddy.server.auth.domain.model.code

fun interface AuthCodeGenerator {
    fun get(): String
}
