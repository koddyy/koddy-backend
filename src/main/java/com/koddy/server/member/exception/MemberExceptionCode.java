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
    AVAILABLE_LANGUAGE_MUST_BE_EXISTS(BAD_REQUEST, "MEMBER_004", "구사 언어는 하나 이상 존재해야 합니다."),
    PERIOD_MUST_EXISTS(BAD_REQUEST, "MEMBER_005", "커피챗 시작/종료 시간을 선택해주세요."),
    START_END_MUST_BE_ALIGN(BAD_REQUEST, "MEMBER_006", "시작 시간이 종료 시간 이후가 될 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
