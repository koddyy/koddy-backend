package com.koddy.server.auth.domain.model.oauth

interface OAuthUserResponse {
    fun id(): String

    fun name(): String

    fun email(): String

    fun profileImageUrl(): String
}
