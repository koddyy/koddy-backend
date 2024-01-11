package com.koddy.server.auth.domain.model;

import static com.koddy.server.member.domain.model.Role.MENTOR;

public record Authenticated(
        String accessToken,
        long id,
        String authority
) {
    public static final String SESSION_KEY = "Koddy";

    public boolean isMentor() {
        return MENTOR.getAuthority().equals(authority);
    }
}
