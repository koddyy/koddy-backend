package com.koddy.server.global.annotation

import com.koddy.server.auth.domain.model.TokenType

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtractToken(val tokenType: TokenType = TokenType.ACCESS)
