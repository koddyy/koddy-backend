package com.koddy.server.coffeechat.exception

import com.koddy.server.global.base.BusinessException

class CoffeeChatException(
    override val code: CoffeeChatExceptionCode,
) : BusinessException(code)
