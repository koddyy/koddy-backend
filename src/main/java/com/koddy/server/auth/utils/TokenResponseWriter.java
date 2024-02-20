package com.koddy.server.auth.utils;

import com.koddy.server.auth.domain.model.AuthToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.TOKEN_TYPE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Component
public class TokenResponseWriter {
    private final long refreshTokenCookieAge;

    public TokenResponseWriter(@Value("${jwt.refresh-token-validity-seconds}") final long refreshTokenCookieAge) {
        this.refreshTokenCookieAge = refreshTokenCookieAge;
    }

    public void applyToken(final HttpServletResponse response, final AuthToken token) {
        applyAccessToken(response, token.accessToken());
        applyRefreshToken(response, token.refreshToken());
    }

    public void expireRefreshTokenCookie(final HttpServletResponse response) {
        final ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_HEADER, "")
                .maxAge(1)
                .sameSite(Cookie.SameSite.NONE.attributeValue())
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.setHeader(SET_COOKIE, cookie.toString());
    }

    private void applyAccessToken(final HttpServletResponse response, final String accessToken) {
        response.setHeader(ACCESS_TOKEN_HEADER, String.join(" ", TOKEN_TYPE, accessToken));
    }

    private void applyRefreshToken(final HttpServletResponse response, final String refreshToken) {
        final ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_HEADER, refreshToken)
                .maxAge(refreshTokenCookieAge)
                .sameSite(Cookie.SameSite.NONE.attributeValue())
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.setHeader(SET_COOKIE, cookie.toString());
    }
}
