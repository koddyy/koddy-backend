package com.koddy.server.auth.utils;

import com.koddy.server.auth.domain.model.AuthToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static org.springframework.boot.web.server.Cookie.SameSite.STRICT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Component
public class TokenResponseWriter {
    public static final String COOKIE_REFRESH_TOKEN = "refresh_token";
    public static final String HEADER_ACCESS_TOKEN_PREFIX = "Bearer";

    private final long refreshTokenCookieAge;

    public TokenResponseWriter(@Value("${jwt.refresh-token-validity-seconds}") final long refreshTokenCookieAge) {
        this.refreshTokenCookieAge = refreshTokenCookieAge;
    }

    public void applyToken(final HttpServletResponse response, final AuthToken token) {
        applyAccessToken(response, token.accessToken());
        applyRefreshToken(response, token.refreshToken());
    }

    private void applyAccessToken(final HttpServletResponse response, final String accessToken) {
        response.setHeader(AUTHORIZATION, String.join(" ", HEADER_ACCESS_TOKEN_PREFIX, accessToken));
    }

    private void applyRefreshToken(final HttpServletResponse response, final String refreshToken) {
        final ResponseCookie cookie = ResponseCookie.from(COOKIE_REFRESH_TOKEN, refreshToken)
                .maxAge(refreshTokenCookieAge)
                .sameSite(STRICT.attributeValue())
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.setHeader(SET_COOKIE, cookie.toString());
    }
}
