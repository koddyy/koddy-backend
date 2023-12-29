package com.koddy.server.global.exception;

import com.koddy.server.global.base.KoddyException;
import com.koddy.server.global.base.KoddyExceptionCode;

public class GlobalException extends KoddyException {
    private final GlobalExceptionCode code;

    public GlobalException(final GlobalExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public KoddyExceptionCode getCode() {
        return code;
    }
}
