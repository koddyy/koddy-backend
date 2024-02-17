package com.koddy.server.global.query

data class PageResponse<T>(
    val result: T,
    val totalCount: Long,
    val hasNext: Boolean,
)
