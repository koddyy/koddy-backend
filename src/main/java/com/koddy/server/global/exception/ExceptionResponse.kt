package com.koddy.server.global.exception

import com.koddy.server.global.base.BusinessExceptionCode

data class ExceptionResponse(
    val errorCode: String,
    val message: String,
) {
    constructor(code: BusinessExceptionCode) : this(
        errorCode = code.errorCode,
        message = code.message,
    )

    constructor(
        code: BusinessExceptionCode,
        message: String,
    ) : this(
        errorCode = code.errorCode,
        message = message,
    )
}
