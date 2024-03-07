package com.koddy.server.file.application.usecase.command

import com.koddy.server.file.domain.model.RawFileData

data class UploadFileCommand(
    val file: RawFileData,
)
