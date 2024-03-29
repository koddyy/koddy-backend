package com.koddy.server.member.application.usecase

import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenIssuer
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.SocialPlatform
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader
import com.koddy.server.member.domain.service.MemberWriter
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.ACCOUNT_ALREADY_EXISTS

@UseCase
class SignUpUseCase(
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter,
    private val tokenIssuer: TokenIssuer,
) {
    fun signUpMentor(command: SignUpMentorCommand): AuthMember {
        validateAccountExists(command.platform)
        val mentor: Mentor = memberWriter.saveMentor(command.toDomain())
        return provideAuthMember(mentor)
    }

    fun signUpMentee(command: SignUpMenteeCommand): AuthMember {
        validateAccountExists(command.platform)
        val mentee: Mentee = memberWriter.saveMentee(command.toDomain())
        return provideAuthMember(mentee)
    }

    private fun validateAccountExists(platform: SocialPlatform) {
        if (memberReader.existsByPlatformSocialId(platform.socialId!!)) {
            throw MemberException(ACCOUNT_ALREADY_EXISTS)
        }
    }

    private fun provideAuthMember(member: Member<*>): AuthMember {
        val authToken: AuthToken = tokenIssuer.provideAuthorityToken(member.id, member.authority)
        return AuthMember(member, authToken)
    }
}
