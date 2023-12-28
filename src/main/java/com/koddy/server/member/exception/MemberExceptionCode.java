package com.koddy.server.member.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode implements KoddyExceptionCode {
    INVALID_EMAIL_PATTERN(BAD_REQUEST, "MEMBER_001", "이메일 형식에 맞지 않습니다."),
    INVALID_PASSWORD_PATTERN(BAD_REQUEST, "MEMBER_002", "비밀번호 형식에 맞지 않습니다."),
    PASSWORD_SAME_AS_BEFORE(CONFLICT, "MEMBER_003", "이전과 동일한 비밀번호로 변경할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
