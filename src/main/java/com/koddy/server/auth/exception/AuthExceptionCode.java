package com.koddy.server.auth.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements KoddyExceptionCode {
    AUTH_REQUIRED(UNAUTHORIZED, "AUTH_001", "인증이 필요합니다."),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH_002", "토큰이 유효하지 않습니다."),
    INVALID_PERMISSION(FORBIDDEN, "AUTH_003", "권한이 없습니다."),
    INVALID_OAUTH_PROVIDER(BAD_REQUEST, "AUTH_004", "제공하지 않는 OAuth Provider입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
