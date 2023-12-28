package com.koddy.server.auth.domain.model;

import com.koddy.server.member.domain.model.RoleType;

import java.util.List;

public record Authenticated(
        Long id,
        List<RoleType> roles
) {
    public static final String SESSION_KEY = "Koddy";
}
