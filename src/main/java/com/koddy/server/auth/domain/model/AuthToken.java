package com.koddy.server.auth.domain.model;

public record AuthToken(
        String accessToken,
        String refreshToken
) {
}
