package com.koddy.server.file.domain.model

import java.io.InputStream

data class RawFileData(
    val fileName: String,
    val contentType: String,
    val extension: FileExtension,
    val content: InputStream,
)
