package com.koddy.server.auth.domain.service

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.auth.domain.model.AuthToken
import org.springframework.stereotype.Service

@Service
class TokenIssuer(
    private val tokenProvider: TokenProvider,
    private val tokenStore: TokenStore,
) {
    fun provideAuthorityToken(
        memberId: Long,
        authority: String,
    ): AuthToken {
        val accessToken: String = tokenProvider.createAccessToken(memberId, authority)
        val refreshToken: String = tokenProvider.createRefreshToken(memberId)
        tokenStore.synchronizeRefreshToken(memberId, refreshToken)
        return AuthToken(accessToken, refreshToken)
    }

    fun reissueAuthorityToken(
        memberId: Long,
        authority: String,
    ): AuthToken {
        val newAccessToken: String = tokenProvider.createAccessToken(memberId, authority)
        val newRefreshToken: String = tokenProvider.createRefreshToken(memberId)
        tokenStore.updateRefreshToken(memberId, newRefreshToken)
        return AuthToken(newAccessToken, newRefreshToken)
    }

    fun isMemberRefreshToken(
        memberId: Long,
        refreshToken: String,
    ): Boolean {
        return tokenStore.isMemberRefreshToken(memberId, refreshToken)
    }

    fun deleteRefreshToken(memberId: Long) {
        return tokenStore.deleteRefreshToken(memberId)
    }
}
