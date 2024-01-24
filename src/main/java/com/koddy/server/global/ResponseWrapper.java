package com.koddy.server.global;

import lombok.Getter;

@Getter
public class ResponseWrapper<T> {
    private final T result;

    private ResponseWrapper(final T result) {
        this.result = result;
    }

    public static <T> ResponseWrapper<T> from(final T result) {
        return new ResponseWrapper<>(result);
    }
}
