package com.koddy.server.common.utils;

import java.util.UUID;

public class OAuthUtils {
    public static final String GOOGLE_PROVIDER = "google";
    public static final String REDIRECT_URI = "http://localhost:3000/login/google";
    public static final String AUTHORIZATION_CODE = UUID.randomUUID().toString().replaceAll("-", "");
    public static final String STATE = UUID.randomUUID().toString().replaceAll("-", "");
}
