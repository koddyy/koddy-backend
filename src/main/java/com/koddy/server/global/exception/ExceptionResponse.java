package com.koddy.server.global.exception;

import com.koddy.server.global.base.BaseExceptionCode;

public record ExceptionResponse(
        String errorCode,
        String message
) {
    public ExceptionResponse(final BaseExceptionCode code) {
        this(code.getErrorCode(), code.getMessage());
    }

    public ExceptionResponse(final BaseExceptionCode code, final String message) {
        this(code.getErrorCode(), message);
    }
}
