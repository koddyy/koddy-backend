package com.koddy.server.auth.domain.model;

import com.koddy.server.member.domain.model.Member;

public record AuthMember(
        Long id,
        String name,
        String profileImageUrl,
        AuthToken token
) {
    public AuthMember(final Member<?> member, final AuthToken token) {
        this(member.getId(), member.getName(), member.getProfileImageUrl(), token);
    }
}
