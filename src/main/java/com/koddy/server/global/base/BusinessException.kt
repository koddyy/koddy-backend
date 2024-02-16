package com.koddy.server.global.base

abstract class BusinessException(
    open val code: BusinessExceptionCode,
) : RuntimeException(code.message)
