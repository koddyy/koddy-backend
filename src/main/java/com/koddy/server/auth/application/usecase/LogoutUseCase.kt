package com.koddy.server.auth.application.usecase

import com.koddy.server.auth.application.usecase.command.LogoutCommand
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository

@UseCase
class LogoutUseCase(
    private val memberRepository: MemberRepository,
    private val tokenIssuer: TokenIssuer,
) {
    fun invoke(command: LogoutCommand) {
        val member: Member<*> = memberRepository.getById(command.memberId)
        tokenIssuer.deleteRefreshToken(member.id)
    }
}
