package com.koddy.server.member.application.usecase;

import com.koddy.server.auth.application.adapter.AuthenticationProcessor;
import com.koddy.server.auth.domain.model.code.AuthKeyGenerator;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.AuthenticationConfirmWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithProofDataCommand;
import com.koddy.server.member.domain.event.MailAuthenticatedEvent;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@UseCase
@RequiredArgsConstructor
public class AuthenticationMentorUnivUseCase {
    private final MentorRepository mentorRepository;
    private final AuthKeyGenerator authKeyGenerator;
    private final AuthenticationProcessor authenticationProcessor;
    private final ApplicationEventPublisher eventPublisher;

    @KoddyWritableTransactional
    public void authWithMail(final AuthenticationWithMailCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        final String authCode = authenticationProcessor.storeAuthCode(createAuthKey(command.schoolMail()));
        eventPublisher.publishEvent(new MailAuthenticatedEvent(mentor.getId(), command.schoolMail(), authCode));
        mentor.authWithMail(command.schoolMail());
    }

    @KoddyWritableTransactional
    public void confirmMailAuthCode(final AuthenticationConfirmWithMailCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        final String authKey = createAuthKey(command.schoolMail());
        authenticationProcessor.verifyAuthCode(authKey, command.authCode());
        authenticationProcessor.deleteAuthCode(authKey);
        mentor.authComplete();
    }

    private String createAuthKey(final String email) {
        return authKeyGenerator.get("MENTOR-MAIL-AUTH:%s", email);
    }

    public void authWithProofData(final AuthenticationWithProofDataCommand command) {

    }
}
