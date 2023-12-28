package com.koddy.server.global.dto;

public record ResponseWrapper<T>(
        T result
) {
    public static <T> ResponseWrapper<T> from(final T result) {
        return new ResponseWrapper<>(result);
    }
}
