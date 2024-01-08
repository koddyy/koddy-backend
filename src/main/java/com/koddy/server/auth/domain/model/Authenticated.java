package com.koddy.server.auth.domain.model;

import java.util.List;

import static com.koddy.server.member.domain.model.RoleType.MENTOR;

public record Authenticated(
        String accessToken,
        Long id,
        List<String> authorities
) {
    public static final String SESSION_KEY = "Koddy";

    public boolean isMentor() {
        return authorities.contains(MENTOR.getAuthority());
    }
}
