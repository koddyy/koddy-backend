package com.koddy.server.auth.domain.model;

import com.koddy.server.member.domain.model.Member;

public record AuthMember(
        MemberInfo member,
        AuthToken token
) {
    public record MemberInfo(
            Long id,
            String name,
            String profileImageUrl
    ) {
        public MemberInfo(final Member<?> member) {
            this(member.getId(), member.getName(), member.getProfileImageUrl());
        }
    }
}
