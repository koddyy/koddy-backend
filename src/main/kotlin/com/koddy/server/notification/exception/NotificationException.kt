package com.koddy.server.notification.exception

import com.koddy.server.global.base.BusinessException

class NotificationException(
    override val code: NotificationExceptionCode,
) : BusinessException(code)
