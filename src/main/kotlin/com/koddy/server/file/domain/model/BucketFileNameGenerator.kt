package com.koddy.server.file.domain.model

fun interface BucketFileNameGenerator {
    fun get(fileName: String): String
}
