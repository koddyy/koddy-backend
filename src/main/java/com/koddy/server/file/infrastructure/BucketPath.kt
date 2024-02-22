package com.koddy.server.file.infrastructure

enum class BucketPath(
    val path: String,
) {
    MEMBER_PROFILE("profiles"),
    ;

    fun completePath(suffix: String): String = "$path/$suffix"
}
