package com.koddy.server.global;

import org.springframework.data.domain.Pageable;

public record PageResponse<T>(
        T result,
        boolean hasNext
) {
    public static boolean hasNext(
            final Pageable pageable,
            final int contentSize,
            final Long totalCount
    ) {
        if (contentSize == pageable.getPageSize()) {
            return (long) contentSize * (pageable.getPageNumber() + 1) != totalCount;
        }
        return false;
    }
}
