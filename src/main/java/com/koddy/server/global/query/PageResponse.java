package com.koddy.server.global.query;

public record PageResponse<T>(
        T result,
        long totalCount,
        boolean hasNext
) {
}
