package com.koddy.server.file.domain.model

import com.koddy.server.file.exception.FileException
import com.koddy.server.file.exception.FileExceptionCode.INVALID_FILE_EXTENSION
import java.util.stream.Stream

enum class FileExtension(
    val value: String,
) {
    // 프로필 사진
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),

    // 멘토 학교 증명자료
    PDF("pdf"),
    ;

    companion object {
        fun from(fileName: String): FileExtension =
            entries.firstOrNull { it.value == extractFileExtension(fileName) }
                ?: throw FileException(INVALID_FILE_EXTENSION)

        fun isImage(fileName: String): Boolean =
            Stream.of(JPG, JPEG, PNG)
                .anyMatch { it.value == extractFileExtension(fileName) }

        fun isPdf(fileName: String): Boolean = PDF.value == extractFileExtension(fileName)

        private fun extractFileExtension(fileName: String): String =
            fileName.substring(fileName.lastIndexOf(".") + 1).lowercase()
    }
}
