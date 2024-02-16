package com.koddy.server.global.exception

import com.koddy.server.global.base.BusinessExceptionCode
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE

enum class GlobalExceptionCode(
    override val status: HttpStatus,
    override val errorCode: String,
    override val message: String,
) : BusinessExceptionCode {
    NOT_SUPPORTED_URI_ERROR(NOT_FOUND, "GLOBAL_001", "제공하지 않는 요청입니다."),
    NOT_SUPPORTED_METHOD_ERROR(METHOD_NOT_ALLOWED, "GLOBAL_002", "제공하지 않는 HTTP Method 요청입니다."),
    VALIDATION_ERROR(BAD_REQUEST, "GLOBAL_003", "잘못된 요청입니다."),
    UNSUPPORTED_MEDIA_TYPE_ERROR(UNSUPPORTED_MEDIA_TYPE, "GLOBAL_004", "잘못된 요청입니다."),
    UNEXPECTED_SERVER_ERROR(INTERNAL_SERVER_ERROR, "GLOBAL_005", "내부 서버 오류입니다.\nKoddy 고객센터에 문의해주세요."),
    NOT_PROVIDED_UNIV_DOMAIN(BAD_REQUEST, "GLOBAL_006", "파악할 수 없는 대학교 도메인입니다.\nKoddy 고객센터에 문의해주세요."),
    INVALID_TIME_DATA(BAD_REQUEST, "GLOBAL_007", "시간 정보는 00:00:00 ~ 23:59:59 범위만 허용합니다"),
}
