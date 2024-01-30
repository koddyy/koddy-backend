package com.koddy.server.global.query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PageCreator {
    int SLICE_PER_PAGE = 10;

    /**
     * Request Page = 1부터 시작
     */
    static Pageable create(final int page) {
        return PageRequest.of(page - 1, SLICE_PER_PAGE);
    }
}
