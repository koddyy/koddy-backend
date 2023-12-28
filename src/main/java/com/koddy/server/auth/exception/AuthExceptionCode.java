package com.koddy.server.auth.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements KoddyExceptionCode {
    INVALID_TOKEN(UNAUTHORIZED, "AUTH_001", "토큰이 유효하지 않습니다."),
    INVALID_PERMISSION(FORBIDDEN, "AUTH_002", "권한이 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}