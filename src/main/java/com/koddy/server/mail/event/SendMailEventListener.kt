package com.koddy.server.mail.event

import com.koddy.server.global.log.logger
import com.koddy.server.mail.application.adapter.EmailSender
import com.koddy.server.member.domain.event.MailAuthenticatedEvent
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class SendMailEventListener(
    private val emailSender: EmailSender,
) {
    private val log: Logger = logger()

    @Async("emailAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendParticipationApproveMail(event: MailAuthenticatedEvent) {
        log.info(
            "Mentor[{}] 학교 메일 인증 -> Mail=[{}], Time=[{}]",
            event.mentorId,
            event.targetEmail,
            event.eventPublishedAt,
        )
        emailSender.sendEmailAuthMail(event.targetEmail, event.authCode)
    }
}
