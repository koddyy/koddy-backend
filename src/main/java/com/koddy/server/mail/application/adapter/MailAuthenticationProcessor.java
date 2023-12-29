package com.koddy.server.mail.application.adapter;

public interface MailAuthenticationProcessor {
    String storeAuthCode(final String key);

    void verifyAuthCode(final String key, final String value);

    void deleteAuthCode(final String key);
}
