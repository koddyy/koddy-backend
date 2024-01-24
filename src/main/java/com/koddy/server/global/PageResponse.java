package com.koddy.server.global;

public record PageResponse<T>(
        T result,
        boolean hasNext
) {
}
