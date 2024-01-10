package com.koddy.server.member.domain.model.mentor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationStatus {
    ATTEMPT("시도"),
    SUCCESS("성공"),
    FAILURE("실패"),
    ;

    private final String value;
}
