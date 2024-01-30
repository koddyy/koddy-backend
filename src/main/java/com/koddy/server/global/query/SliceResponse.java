package com.koddy.server.global.query;

public record SliceResponse<T>(
        T result,
        boolean hasNext
) {
}
