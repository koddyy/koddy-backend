package com.koddy.server.global.query

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

object PageCreator {
    private const val SLICE_PER_PAGE: Int = 10

    /**
     * Request Page = 1부터 시작
     */
    fun create(page: Int): Pageable = PageRequest.of(page - 1, SLICE_PER_PAGE)
}
