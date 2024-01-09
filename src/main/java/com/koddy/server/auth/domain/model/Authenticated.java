package com.koddy.server.auth.domain.model;

import com.koddy.server.member.domain.model.Role;

import java.util.List;

public record Authenticated(
        String accessToken,
        Long id,
        List<String> authorities
) {
    public static final String SESSION_KEY = "Koddy";

    public boolean isMentor() {
        return authorities.contains(Role.Type.MENTOR.getAuthority());
    }
}
