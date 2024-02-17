package com.koddy.server.auth.exception

import com.koddy.server.global.base.BusinessException

class AuthException(
    override val code: AuthExceptionCode,
) : BusinessException(code)
