package com.koddy.server.auth.exception;

import com.koddy.server.global.base.KoddyException;
import com.koddy.server.global.base.KoddyExceptionCode;

public class AuthException extends KoddyException {
    private final AuthExceptionCode code;

    public AuthException(final AuthExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public KoddyExceptionCode getCode() {
        return code;
    }
}
