package com.koddy.server.member.domain.model.mentor;

public enum AuthenticationStatus {
    ATTEMPT("시도"),
    SUCCESS("성공"),
    FAILURE("실패"),
    ;

    private final String value;

    AuthenticationStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
