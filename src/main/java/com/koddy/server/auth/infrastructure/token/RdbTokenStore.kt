package com.koddy.server.auth.infrastructure.token

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.auth.domain.model.Token
import com.koddy.server.auth.domain.repository.TokenRepository
import com.koddy.server.global.annotation.KoddyWritableTransactional
import org.springframework.stereotype.Component

@Component
class RdbTokenStore(
    private val tokenRepository: TokenRepository,
) : TokenStore {
    @KoddyWritableTransactional
    override fun synchronizeRefreshToken(
        memberId: Long,
        refreshToken: String,
    ) {
        tokenRepository.findByMemberId(memberId)
            .ifPresentOrElse(
                { it.updateRefreshToken(refreshToken) },
                { tokenRepository.save(Token(memberId, refreshToken)) },
            )
    }

    override fun updateRefreshToken(
        memberId: Long,
        newRefreshToken: String,
    ) = tokenRepository.updateRefreshToken(memberId, newRefreshToken)

    override fun deleteRefreshToken(memberId: Long) = tokenRepository.deleteRefreshToken(memberId)

    override fun isMemberRefreshToken(
        memberId: Long,
        refreshToken: String,
    ): Boolean = tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken)
}
