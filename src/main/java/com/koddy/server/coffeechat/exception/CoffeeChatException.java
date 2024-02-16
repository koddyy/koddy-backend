package com.koddy.server.coffeechat.exception;

import com.koddy.server.global.base.BaseException;
import com.koddy.server.global.base.BaseExceptionCode;

public class CoffeeChatException extends BaseException {
    private final CoffeeChatExceptionCode code;

    public CoffeeChatException(final CoffeeChatExceptionCode code) {
        super(code);
        this.code = code;
    }

    @Override
    public BaseExceptionCode getCode() {
        return code;
    }
}
