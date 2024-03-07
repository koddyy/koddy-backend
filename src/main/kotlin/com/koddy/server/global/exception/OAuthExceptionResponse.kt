package com.koddy.server.global.exception

import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse

data class OAuthExceptionResponse(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String,
) {
    constructor(oAuthUserResponse: OAuthUserResponse) : this(
        id = oAuthUserResponse.id(),
        name = oAuthUserResponse.name(),
        email = oAuthUserResponse.email(),
        profileImageUrl = oAuthUserResponse.profileImageUrl(),
    )
}
