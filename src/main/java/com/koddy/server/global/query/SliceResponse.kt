package com.koddy.server.global.query

data class SliceResponse<T>(
    val result: T,
    val hasNext: Boolean,
)
