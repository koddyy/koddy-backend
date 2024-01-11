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

    private final String provider;

    public static OAuthProvider from(final String provider) {
        return Arrays.stream(values())
                .filter(it -> it.provider.equals(provider))
                .findFirst()
                .orElseThrow(() -> new AuthException(INVALID_OAUTH_PROVIDER));
    }
}
