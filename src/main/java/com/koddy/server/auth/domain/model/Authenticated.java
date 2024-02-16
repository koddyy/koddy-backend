package com.koddy.server.auth.domain.model;

import static com.koddy.server.member.domain.model.Role.MENTEE;
import static com.koddy.server.member.domain.model.Role.MENTOR;

public record Authenticated(
        long id,
        String authority
) {
    public boolean isMentor() {
        return MENTOR.getAuthority().equals(authority);
    }

    public boolean isMentee() {
        return MENTEE.getAuthority().equals(authority);
    }
}
