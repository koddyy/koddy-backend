package com.koddy.server.auth.application.usecase

import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository

@UseCase
class ReissueTokenUseCase(
    private val memberRepository: MemberRepository,
    private val tokenProvider: TokenProvider,
    private val tokenIssuer: TokenIssuer,
) {
    fun invoke(refreshToken: String): AuthToken {
        val member: Member<*> = getMember(refreshToken)
        validateMemberToken(member.id, refreshToken)
        return tokenIssuer.reissueAuthorityToken(member.id, member.authority)
    }

    private fun getMember(refreshToken: String): Member<*> {
        val memberId: Long = tokenProvider.getId(refreshToken)
        return memberRepository.getById(memberId)
    }

    private fun validateMemberToken(
        memberId: Long,
        refreshToken: String,
    ) {
        if (tokenIssuer.isMemberRefreshToken(memberId, refreshToken).not()) {
            throw AuthException(INVALID_TOKEN)
        }
    }
}
