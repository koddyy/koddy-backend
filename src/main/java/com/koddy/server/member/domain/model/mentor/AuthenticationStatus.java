package com.koddy.server.member.domain.model.mentor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationStatus {
    ATTEMPT("시도"),
    COMPLETE("완료"),
    ;

    private final String value;
}
