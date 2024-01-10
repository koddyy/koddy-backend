package com.koddy.server.common.mock.stub;

import com.koddy.server.mail.application.adapter.EmailSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StubEmailSender implements EmailSender {
    @Override
    public void sendEmailAuthMail(final String targetEmail, final String authCode) {
        log.info("학교 인증 메일 발송 -> Email=[{}], AuthCode=[{}]", targetEmail, authCode);
    }
}
