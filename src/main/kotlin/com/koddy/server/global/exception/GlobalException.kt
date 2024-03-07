package com.koddy.server.global.exception

import com.koddy.server.global.base.BusinessException

class GlobalException(
    override val code: GlobalExceptionCode,
) : BusinessException(code)
