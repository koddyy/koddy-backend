package com.koddy.server.member.exception;

import com.koddy.server.global.base.KoddyException;
import com.koddy.server.global.base.KoddyExceptionCode;

public class MemberException extends KoddyException {
    private final MemberExceptionCode code;

    public MemberException(final MemberExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public KoddyExceptionCode getCode() {
        return code;
    }
}
