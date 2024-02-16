package com.koddy.server.global.query

@JvmRecord
data class PageResponse<T>(
    val result: T,
    val totalCount: Long,
    val hasNext: Boolean,
)
