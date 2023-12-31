package com.koddy.server.common.utils;

import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import jakarta.servlet.http.Cookie;

import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;

public class TokenUtils {
    public static final String BEARER_TOKEN = "Bearer";
    public static final String ID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjc3OTM3MjI0LCJleHAiOjE2Nzc5NDQ0MjR9.t61tw4gDEBuXBn_DnCwiPIDaI-KcN9Zkn3QJSEK7fag";
    public static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwicm9sZXMiOlsiTUVOVE9SIiwiTUVOVEVFIl0sImlhdCI6MTcwMzc0NjI1NywiZXhwIjozMjgwNTQ2MjU3fQ.hJaFVG4SgXNoVs_6GB6yoPSTr7WO8n30LD0RSgmcyPY";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNzAzNzQ2MjU3LCJleHAiOjMyODA1NDYyNTd9.64LBOtAZIjO4FMpJoxfQTSBLp5azilb7wjk0hpa1Pz8";
    public static final long EXPIRES_IN = 3000;

    public static String applyAccessToken() {
        return String.join(" ", BEARER_TOKEN, ACCESS_TOKEN);
    }

    public static Cookie applyRefreshToken() {
        return new Cookie(COOKIE_REFRESH_TOKEN, REFRESH_TOKEN);
    }

    public static GoogleTokenResponse createGoogleTokenResponse() {
        return new GoogleTokenResponse(
                BEARER_TOKEN,
                ID_TOKEN,
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                EXPIRES_IN
        );
    }
}
