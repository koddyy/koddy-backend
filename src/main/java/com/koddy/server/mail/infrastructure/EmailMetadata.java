package com.koddy.server.mail.infrastructure;

public interface EmailMetadata {
    // 템플릿 이름
    String AUTH_TEMPLATE = "EmailAuthCodeTemplate";

    // 이메일 제목
    String KODDY_AUTH_CODE_TITLE = "Koddy 계정 인증 메일입니다.";
}
