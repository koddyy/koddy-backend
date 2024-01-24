package com.koddy.server.global;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PageCreator {
    int SLICE_PER_PAGE = 10;

    static Pageable create(final int page) {
        return PageRequest.of(page - 1, SLICE_PER_PAGE); // 0부터 페이지 시작
    }
}
