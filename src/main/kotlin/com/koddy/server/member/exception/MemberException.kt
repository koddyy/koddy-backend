package com.koddy.server.member.exception

import com.koddy.server.global.base.BusinessException

class MemberException(
    override val code: MemberExceptionCode,
) : BusinessException(code)
