package com.koddy.server.common.utils;

import com.koddy.server.auth.infrastructure.social.google.response.GoogleTokenResponse;
import jakarta.servlet.http.Cookie;

import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.TOKEN_TYPE;

public class TokenUtils {
    public static final String ID_TOKEN = "ID-TOKEN";
    public static final String ACCESS_TOKEN = "ACCESS-TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH-TOKEN";
    public static final long EXPIRES_IN = 3000;

    public static String applyAccessToken() {
        return String.join(" ", TOKEN_TYPE, ACCESS_TOKEN);
    }

    public static Cookie applyRefreshToken() {
        return new Cookie(REFRESH_TOKEN_HEADER, REFRESH_TOKEN);
    }

    public static GoogleTokenResponse createGoogleTokenResponse() {
        return new GoogleTokenResponse(
                TOKEN_TYPE,
                ID_TOKEN,
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN
        );
    }
}
