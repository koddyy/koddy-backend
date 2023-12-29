package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.AuthenticationProcessor;
import com.koddy.server.auth.application.usecase.command.ConfirmAuthCodeCommand;
import com.koddy.server.auth.application.usecase.command.SendAuthCodeCommand;
import com.koddy.server.auth.domain.model.code.AuthKey;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.mail.application.adapter.EmailSender;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class EmailAuthenticationUseCase {
    private final MemberRepository memberRepository;
    private final AuthenticationProcessor authenticationProcessor;
    private final EmailSender emailSender;

    public void sendAuthCode(final SendAuthCodeCommand command) {
        final Member<?> member = memberRepository.getByEmail(command.email());

        final String key = generateAuthKey(member.getEmail().getValue());
        final String authCode = authenticationProcessor.storeAuthCode(key);
        emailSender.sendEmailAuthMail(member.getEmail().getValue(), authCode);
    }

    public void confirmAuthCode(final ConfirmAuthCodeCommand command) {
        final Member<?> member = memberRepository.getByEmail(command.email());
        verifyAuthCode(member, command.authCode());
    }

    private void verifyAuthCode(final Member<?> member, final String authCode) {
        final String key = generateAuthKey(member.getEmail().getValue());
        authenticationProcessor.verifyAuthCode(key, authCode);
        authenticationProcessor.deleteAuthCode(key); // 인증 성공 후 바로 제거 (재활용 X)
    }

    private String generateAuthKey(final String email) {
        return AuthKey.EMAIL.generateAuthKey(email);
    }
}
