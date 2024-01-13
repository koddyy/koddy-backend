package com.koddy.server.auth.domain.model.oauth;

import com.koddy.server.auth.exception.AuthException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_OAUTH_PROVIDER;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    GOOGLE("google"),
    KAKAO("kakao"),
    ZOOM("zoom"),
    ;

    private final String value;

    public static OAuthProvider from(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new AuthException(INVALID_OAUTH_PROVIDER));
    }
}
