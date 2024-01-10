package com.koddy.server.auth.application.adapter;

public interface AuthenticationProcessor {
    String storeAuthCode(final String key);

    void verifyAuthCode(final String key, final String value);

    void deleteAuthCode(final String key);
}
