package com.koddy.server.notification.application.usecase

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.notification.application.usecase.command.ReadSingleNotificationCommand
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.repository.NotificationRepository

@UseCase
class ReadNotificationUseCase(
    private val notificationRepository: NotificationRepository,
) {
    @KoddyWritableTransactional
    fun readSingle(command: ReadSingleNotificationCommand) {
        val notification: Notification = notificationRepository.getByIdAndTargetId(
            command.notificationId,
            command.memberId,
        )
        notification.read()
    }

    fun readAll(memberId: Long) {
        notificationRepository.readAll(memberId)
    }
}
