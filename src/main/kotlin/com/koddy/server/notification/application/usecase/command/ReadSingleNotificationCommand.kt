package com.koddy.server.notification.application.usecase.command

data class ReadSingleNotificationCommand(
    val memberId: Long,
    val notificationId: Long,
)
