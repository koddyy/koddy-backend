package com.koddy.server.common.utils

import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream
import java.io.IOException

object FileVirtualCreator {
    private const val FILE_PATH = "src/test/resources/files/"

    fun createFile(
        fileName: String,
        contentType: String,
    ): MultipartFile {
        try {
            FileInputStream(FILE_PATH + fileName).use {
                return MockMultipartFile("file", fileName, contentType, it)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
