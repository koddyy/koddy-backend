package com.koddy.server.auth.exception;

import com.koddy.server.global.base.BaseException;
import com.koddy.server.global.base.BaseExceptionCode;

public class AuthException extends BaseException {
    private final AuthExceptionCode code;

    public AuthException(final AuthExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public BaseExceptionCode getCode() {
        return code;
    }
}
