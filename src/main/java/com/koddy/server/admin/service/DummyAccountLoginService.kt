package com.koddy.server.admin.service

import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.Email
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode
import org.springframework.beans.factory.annotation.Value

@UseCase
class DummyAccountLoginService(
    private val memberRepository: MemberRepository,
    private val tokenIssuer: TokenIssuer,
    @Value("\${account.dummy}") private val dummyAccountPassword: String,
) {
    fun invoke(
        email: Email,
        password: String,
    ): AuthMember {
        val member: Member<*> = memberRepository.findByPlatformEmail(email)
            .orElseThrow { MemberException(MemberExceptionCode.MEMBER_NOT_FOUND) }

        if (dummyAccountPassword != password) {
            throw MemberException(MemberExceptionCode.MEMBER_NOT_FOUND)
        }

        val authToken: AuthToken = tokenIssuer.provideAuthorityToken(member.id, member.authority)
        return AuthMember(member, authToken)
    }
}
