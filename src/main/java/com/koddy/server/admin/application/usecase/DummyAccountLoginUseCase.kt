package com.koddy.server.admin.application.usecase

import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.Email
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND
import org.springframework.beans.factory.annotation.Value

@UseCase
class DummyAccountLoginUseCase(
    private val memberRepository: MemberRepository,
    private val tokenIssuer: TokenIssuer,
    @Value("\${account.dummy}") private val dummyAccountPassword: String,
) {
    fun invoke(
        email: Email,
        password: String,
    ): AuthToken {
        val member: Member<*> = memberRepository.findByPlatformEmail(email)
            .orElseThrow { MemberException(MEMBER_NOT_FOUND) }

        if (dummyAccountPassword != password) {
            throw MemberException(MEMBER_NOT_FOUND)
        }

        return tokenIssuer.provideAuthorityToken(member.id, member.authority)
    }
}
