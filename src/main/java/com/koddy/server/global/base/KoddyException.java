package com.koddy.server.global.base;

import lombok.Getter;

@Getter
public abstract class KoddyException extends RuntimeException {
    private final KoddyExceptionCode code;

    protected KoddyException(final KoddyExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
