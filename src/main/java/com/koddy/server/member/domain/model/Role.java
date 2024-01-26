package com.koddy.server.member.domain.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    MENTOR("ROLE_MENTOR", "멘토"),
    MENTEE("ROLE_MENTEE", "멘티"),
    ADMIN("ROLE_ADMIN", "관리자"),
    ;

    private final String authority;
    private final String value;

    public static class Value {
        public static final String MENTOR = "MENTOR";
        public static final String MENTEE = "MENTEE";
    }
}
