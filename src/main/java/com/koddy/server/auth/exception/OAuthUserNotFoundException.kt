package com.koddy.server.auth.exception

import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse

class OAuthUserNotFoundException(
    val response: OAuthUserResponse,
) : RuntimeException()
