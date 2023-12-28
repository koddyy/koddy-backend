package com.koddy.server.global.exception;

import com.koddy.server.global.base.KoddyExceptionCode;

public record ExceptionResponse(
        String errorCode,
        String message
) {
    public ExceptionResponse(final KoddyExceptionCode code) {
        this(code.getErrorCode(), code.getMessage());
    }

    public ExceptionResponse(final KoddyExceptionCode code, final String message) {
        this(code.getErrorCode(), message);
    }
}
