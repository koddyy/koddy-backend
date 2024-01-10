package com.koddy.server.mail.event;

import com.koddy.server.mail.application.adapter.EmailSender;
import com.koddy.server.member.domain.event.MailAuthenticatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendMailEventListener {
    private final EmailSender emailSender;

    @Async("emailAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendParticipationApproveMail(final MailAuthenticatedEvent event) {
        log.info(
                "Mentor[{}] 학교 메일 인증 -> Mail=[{}], Time=[{}]",
                event.getMentorId(),
                event.getTargetEmail(),
                event.getEventPublishedAt()
        );
        emailSender.sendEmailAuthMail(event.getTargetEmail(), event.getAuthCode());
    }
}
