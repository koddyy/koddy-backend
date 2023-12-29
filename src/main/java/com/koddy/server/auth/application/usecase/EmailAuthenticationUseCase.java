package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.AuthenticationProcessor;
import com.koddy.server.auth.application.usecase.command.SendAuthCodeCommand;
import com.koddy.server.auth.application.usecase.command.VerifyAuthCodeCommand;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.mail.application.adapter.EmailSender;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class EmailAuthenticationUseCase {
    private final MemberRepository memberRepository;
    private final AuthenticationProcessor authenticationProcessor;
    private final EmailSender emailSender;

    public void sendAuthCode(final SendAuthCodeCommand command) {

    }

    public void verifyAuthCode(final VerifyAuthCodeCommand command) {

    }
}
