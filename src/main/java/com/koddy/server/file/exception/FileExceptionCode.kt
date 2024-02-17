package com.koddy.server.file.exception

import com.koddy.server.global.base.BusinessExceptionCode
import org.springframework.http.HttpStatus

enum class FileExceptionCode(
    override val status: HttpStatus,
    override val errorCode: String,
    override val message: String,
) : BusinessExceptionCode {
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "FILE_001", "파일 확장자는 [JPG, JPEG, PNG]만 가능합니다"),
    FILE_NOT_UPLOADED(HttpStatus.BAD_REQUEST, "FILE_002", "파일이 업로드되지 않았습니다."),
}
