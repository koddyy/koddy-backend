package com.koddy.server.common.utils;

import com.koddy.server.auth.infrastructure.oauth.google.GoogleOAuthProperties;
import com.koddy.server.auth.infrastructure.oauth.kakao.KakaoOAuthProperties;
import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthProperties;

import java.util.Set;
import java.util.UUID;

public class OAuthUtils {
    public static final String GOOGLE_PROVIDER = "google";
    public static final String REDIRECT_URI = "http://localhost:3000/login";
    public static final String AUTHORIZATION_CODE = UUID.randomUUID().toString().replaceAll("-", "");
    public static final String STATE = UUID.randomUUID().toString().replaceAll("-", "");

    public static GoogleOAuthProperties getGoogleOAuthProperties() {
        return new GoogleOAuthProperties(
                "authorization_code",
                "client_id",
                "client_secret",
                "http://localhost:3000/login/google",
                Set.of("openid", "https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email"),
                "https://accounts.google.com/o/oauth2/v2/auth",
                "https://www.googleapis.com/oauth2/v4/token",
                "https://www.googleapis.com/oauth2/v3/userinfo"
        );
    }

    public static KakaoOAuthProperties getKakaoOAuthProperties() {
        return new KakaoOAuthProperties(
                "authorization_code",
                "client_id",
                "client_secret",
                "http://localhost:3000/login/kakao",
                Set.of("profile_nickname", "profile_image", "account_email"),
                "https://kauth.kakao.com/oauth/authorize",
                "https://kauth.kakao.com/oauth/token",
                "https://kapi.kakao.com/v2/user/me"
        );
    }

    public static ZoomOAuthProperties getZoomOAuthProperties() {
        return new ZoomOAuthProperties(
                "authorization_code",
                "client_id",
                "client_secret",
                "http://localhost:3000/login/zoom",
                "https://zoom.us/oauth/authorize",
                "https://zoom.us/oauth/token",
                "https://api.zoom.us/v2/users/me",
                new ZoomOAuthProperties.Other(
                        "https://api.zoom.us/v2/users/me/meetings",
                        "https://api.zoom.us/v2/meetings/{meetingId}"
                )
        );
    }
}
