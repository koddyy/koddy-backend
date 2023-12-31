package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.MailAuthenticationProcessor;
import com.koddy.server.auth.application.usecase.command.ConfirmAuthCodeCommand;
import com.koddy.server.auth.application.usecase.command.SendAuthCodeCommand;
import com.koddy.server.auth.domain.model.code.AuthKey;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.mail.application.adapter.EmailSender;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class EmailAuthenticationUseCase {
    private final MemberRepository memberRepository;
    private final MailAuthenticationProcessor mailAuthenticationProcessor;
    private final EmailSender emailSender;

    public void sendAuthCode(final SendAuthCodeCommand command) {
        final Member<?> member = memberRepository.getByEmail(command.email());

        final String key = generateAuthKey(member.getEmail().getValue());
        final String authCode = mailAuthenticationProcessor.storeAuthCode(key);
        emailSender.sendEmailAuthMail(member.getEmail().getValue(), authCode);
    }

    @KoddyWritableTransactional
    public void confirmAuthCode(final ConfirmAuthCodeCommand command) {
        final Member<?> member = memberRepository.getByEmail(command.email());

        final String key = generateAuthKey(member.getEmail().getValue());
        mailAuthenticationProcessor.verifyAuthCode(key, command.authCode());
        mailAuthenticationProcessor.deleteAuthCode(key); // 인증 성공 후 바로 제거 (재활용 X)

        member.authenticate();
    }

    private String generateAuthKey(final String email) {
        return AuthKey.EMAIL.generateAuthKey(email);
    }
}
