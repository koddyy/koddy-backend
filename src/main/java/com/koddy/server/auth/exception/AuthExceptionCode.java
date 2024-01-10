package com.koddy.server.auth.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements KoddyExceptionCode {
    AUTH_REQUIRED(UNAUTHORIZED, "AUTH_001", "인증이 필요합니다."),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH_002", "토큰이 유효하지 않습니다."),
    INVALID_PERMISSION(FORBIDDEN, "AUTH_003", "권한이 없습니다."),
    INVALID_OAUTH_PROVIDER(BAD_REQUEST, "AUTH_004", "제공하지 않는 OAuth Provider입니다."),
    INVALID_AUTH_CODE(BAD_REQUEST, "AUTH_005", "인증번호가 일치하지 않습니다."),
    TOO_MANY_MAIL_AUTH_ATTEMPTS(TOO_MANY_REQUESTS, "AUTH_006", "일정 시간 동안 메일 인증 요청 횟수가 너무 많습니다.\n잠시 후 다시 요청해주세요."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
