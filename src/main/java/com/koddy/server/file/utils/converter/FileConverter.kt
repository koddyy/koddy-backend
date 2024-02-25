package com.koddy.server.file.utils.converter

import com.koddy.server.file.domain.model.FileExtension
import com.koddy.server.file.domain.model.RawFileData
import com.koddy.server.file.exception.FileException
import com.koddy.server.file.exception.FileExceptionCode.FILE_NOT_UPLOADED
import com.koddy.server.file.exception.FileExceptionCode.UNRECOGNIZABLE_FILE
import org.springframework.web.multipart.MultipartFile

object FileConverter {
    fun convertFile(file: MultipartFile?): RawFileData {
        if (file == null || file.isEmpty) {
            throw FileException(FILE_NOT_UPLOADED)
        }

        val fileName: String? = file.originalFilename

        return RawFileData(
            fileName = fileName ?: throw FileException(UNRECOGNIZABLE_FILE),
            contentType = file.contentType ?: throw FileException(UNRECOGNIZABLE_FILE),
            extension = FileExtension.from(fileName),
            content = file.inputStream,
        )
    }
}
