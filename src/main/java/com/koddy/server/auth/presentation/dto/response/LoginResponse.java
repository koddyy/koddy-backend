package com.koddy.server.auth.presentation.dto.response;

public record LoginResponse(
        Long id,
        String name,
        String profileImageUrl
) {
}
