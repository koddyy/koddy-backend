package com.koddy.server.mail.application.adapter;

public interface EmailSender {
    void sendEmailAuthMail(final String targetEmail, final String authCode);

    // 템플릿 이름
    String AUTH_TEMPLATE = "EmailAuthCodeTemplate";

    // 이메일 제목
    String KODDY_AUTH_CODE_TITLE = "Koddy 학교 인증 메일입니다.";
}
