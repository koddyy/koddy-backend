package com.koddy.server.file.domain.model

import com.koddy.server.file.exception.FileException
import com.koddy.server.file.exception.FileExceptionCode.INVALID_FILE_EXTENSION

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
        fun from(fileName: String): FileExtension {
            return entries.firstOrNull { it.value.equals(extractFileExtension(fileName), ignoreCase = true) }
                ?: throw FileException(INVALID_FILE_EXTENSION)
        }

        fun isImage(fileName: String): Boolean {
            return listOf(JPG, JPEG, PNG)
                .any { it.value.equals(extractFileExtension(fileName), ignoreCase = true) }
        }

        fun isPdf(fileName: String): Boolean {
            return listOf(PDF)
                .any { it.value.equals(extractFileExtension(fileName), ignoreCase = true) }
        }

        private fun extractFileExtension(fileName: String): String = fileName.substring(fileName.lastIndexOf(".") + 1)
    }
}
