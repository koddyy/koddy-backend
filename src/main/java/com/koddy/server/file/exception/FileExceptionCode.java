package com.koddy.server.file.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileExceptionCode implements KoddyExceptionCode {
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
