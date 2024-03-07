package com.koddy.server.file.domain.model

data class PresignedUrlDetails(
    val preSignedUrl: String,
    val uploadFileUrl: String,
)
