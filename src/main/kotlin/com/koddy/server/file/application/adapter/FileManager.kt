package com.koddy.server.file.application.adapter

import com.koddy.server.file.domain.model.PresignedFileData
import com.koddy.server.file.domain.model.PresignedUrlDetails
import com.koddy.server.file.domain.model.RawFileData

interface FileManager {
    fun createPresignedUrl(file: PresignedFileData): PresignedUrlDetails

    fun upload(file: RawFileData): String
}
