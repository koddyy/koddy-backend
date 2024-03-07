package com.koddy.server.notification.exception

import com.koddy.server.global.base.BusinessExceptionCode
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND

enum class NotificationExceptionCode(
    override val status: HttpStatus,
    override val errorCode: String,
    override val message: String,
) : BusinessExceptionCode {
    NOTIFICATION_NOT_FOUND(NOT_FOUND, "NOTIFICATION_001", "알림 정보가 존재하지 않습니다."),
}
