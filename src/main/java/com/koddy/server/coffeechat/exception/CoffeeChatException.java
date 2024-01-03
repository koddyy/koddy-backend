package com.koddy.server.coffeechat.exception;

import com.koddy.server.global.base.KoddyException;
import com.koddy.server.global.base.KoddyExceptionCode;

public class CoffeeChatException extends KoddyException {
    private final CoffeeChatExceptionCode code;

    public CoffeeChatException(final CoffeeChatExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public KoddyExceptionCode getCode() {
        return code;
    }
}
