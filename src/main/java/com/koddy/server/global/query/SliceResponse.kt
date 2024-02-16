package com.koddy.server.global.query

@JvmRecord
data class SliceResponse<T>(
    val result: T,
    val hasNext: Boolean,
)
