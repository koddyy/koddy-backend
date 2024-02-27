package com.koddy.server.auth.application.usecase

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor
import com.koddy.server.auth.application.usecase.command.OAuthLoginCommand
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.auth.exception.OAuthUserNotFoundException
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository

@UseCase
class OAuthLoginUseCase(
    private val oAuthLoginProcessor: OAuthLoginProcessor,
    private val memberRepository: MemberRepository,
    private val tokenIssuer: TokenIssuer,
) {
    fun invoke(command: OAuthLoginCommand): AuthMember {
        val member: Member<*> = getMemberBySocialLogin(command)
        val authToken: AuthToken = tokenIssuer.provideAuthorityToken(member.id, member.authority)
        return AuthMember(member, authToken)
    }

    private fun getMemberBySocialLogin(command: OAuthLoginCommand): Member<*> {
        val oAuthUser: OAuthUserResponse = oAuthLoginProcessor.login(
            provider = command.provider,
            code = command.code,
            redirectUri = command.redirectUrl,
            state = command.state,
        )
        return memberRepository.findByPlatformSocialId(oAuthUser.id())
            .orElseThrow { OAuthUserNotFoundException(oAuthUser) }
    }
}
