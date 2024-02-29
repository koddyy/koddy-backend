package com.koddy.server.member.application.usecase

import com.koddy.server.auth.application.adapter.AuthenticationProcessor
import com.koddy.server.auth.domain.model.code.AuthKeyGenerator
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.command.AttemptWithMailCommand
import com.koddy.server.member.application.usecase.command.AttemptWithProofDataCommand
import com.koddy.server.member.application.usecase.command.ConfirmMailAuthCodeCommand
import com.koddy.server.member.domain.event.MailAuthenticatedEvent
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MentorRepository
import org.springframework.context.ApplicationEventPublisher

@UseCase
class AuthenticateMentorUnivUseCase(
    private val mentorRepository: MentorRepository,
    private val authKeyGenerator: AuthKeyGenerator,
    private val authenticationProcessor: AuthenticationProcessor,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @KoddyWritableTransactional
    fun attemptWithMail(command: AttemptWithMailCommand) {
        val mentor: Mentor = mentorRepository.getById(command.mentorId)
        val authkey: String = createAuthKey(mentor.id, command.schoolMail)
        val authCode: String = authenticationProcessor.storeAuthCode(authkey)

        eventPublisher.publishEvent(MailAuthenticatedEvent(mentor.id, command.schoolMail, authCode))
        mentor.authWithMail(command.schoolMail)
    }

    @KoddyWritableTransactional
    fun confirmMailAuthCode(command: ConfirmMailAuthCodeCommand) {
        val mentor: Mentor = mentorRepository.getById(command.mentorId)
        val authKey: String = createAuthKey(mentor.id, command.schoolMail)

        authenticationProcessor.verifyAuthCode(authKey, command.authCode)
        authenticationProcessor.deleteAuthCode(authKey)
        mentor.authComplete()
    }

    private fun createAuthKey(
        memberId: Long,
        email: String,
    ): String = authKeyGenerator.get(AUTH_KEY, memberId, email)

    @KoddyWritableTransactional
    fun attemptWithProofData(command: AttemptWithProofDataCommand) {
        val mentor: Mentor = mentorRepository.getById(command.mentorId)
        mentor.authWithProofData(command.proofDataUploadUrl)
    }

    companion object {
        private const val AUTH_KEY: String = "MENTOR-MAIL-AUTH:%d:%s"
    }
}
