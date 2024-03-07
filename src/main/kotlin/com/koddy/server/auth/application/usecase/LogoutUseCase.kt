package com.koddy.server.auth.application.usecase

import com.koddy.server.auth.application.usecase.command.LogoutCommand
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.service.MemberReader

@UseCase
class LogoutUseCase(
    private val memberReader: MemberReader,
    private val tokenIssuer: TokenIssuer,
) {
    fun invoke(command: LogoutCommand) {
        val member: Member<*> = memberReader.getMember(command.memberId)
        tokenIssuer.deleteRefreshToken(member.id)
    }
}
