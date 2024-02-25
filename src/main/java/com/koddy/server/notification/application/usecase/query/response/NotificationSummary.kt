package com.koddy.server.notification.application.usecase.query.response

import com.koddy.server.notification.domain.repository.query.response.NotificationDetails
import java.time.LocalDate
import java.time.LocalDateTime

data class NotificationSummary(
    val id: Long,
    val read: Boolean,
    val coffeeChatStatusSnapshot: String,
    val type: String,
    val createdAt: LocalDateTime,
    val member: NotifyMember,
    val coffeeChat: NotifyCoffeeChat,
) {
    companion object {
        fun from(details: NotificationDetails): NotificationSummary {
            return NotificationSummary(
                id = details.id,
                read = details.read,
                coffeeChatStatusSnapshot = details.coffeeChatStatusSnapshot,
                type = details.type.name,
                createdAt = details.createdAt,
                member = NotifyMember(
                    id = details.memberId,
                    name = details.memberName,
                    profileImageUrl = details.memberProfileImageUrl,
                ),
                coffeeChat = NotifyCoffeeChat(
                    id = details.coffeeChatId,
                    cancelReason = details.coffeeChatReason?.cancelReason,
                    rejectReason = details.coffeeChatReason?.rejectReason,
                    reservedDay = details.coffeeChatReservation?.start?.toLocalDate(),
                ),
            )
        }
    }
}

data class NotifyMember(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
)

data class NotifyCoffeeChat(
    val id: Long,
    val cancelReason: String?,
    val rejectReason: String?,
    val reservedDay: LocalDate?,
)
