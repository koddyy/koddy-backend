package com.koddy.server.global.exception;

import com.koddy.server.global.base.BaseException;
import com.koddy.server.global.base.BaseExceptionCode;

public class GlobalException extends BaseException {
    private final GlobalExceptionCode code;

    public GlobalException(final GlobalExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public BaseExceptionCode getCode() {
        return code;
    }
}
