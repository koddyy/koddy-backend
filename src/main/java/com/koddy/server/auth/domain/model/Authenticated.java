package com.koddy.server.auth.domain.model;

import java.util.List;

public record Authenticated(
        String accessToken,
        Long id,
        List<String> authorities
) {
    public static final String SESSION_KEY = "Koddy";
}
