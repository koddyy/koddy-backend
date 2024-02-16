package com.koddy.server.member.exception;

import com.koddy.server.global.base.BaseException;
import com.koddy.server.global.base.BaseExceptionCode;

public class MemberException extends BaseException {
    private final MemberExceptionCode code;

    public MemberException(final MemberExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public BaseExceptionCode getCode() {
        return code;
    }
}
