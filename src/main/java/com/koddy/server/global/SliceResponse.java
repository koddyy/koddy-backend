package com.koddy.server.global;

public record SliceResponse<T>(
        T result,
        boolean hasNext
) {
}
