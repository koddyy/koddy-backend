package com.koddy.server.notification.domain.repository.query.response

import com.koddy.server.coffeechat.domain.model.Reason
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.notification.domain.model.NotificationType
import java.time.LocalDateTime

data class NotificationDetails(
    val id: Long,
    val isRead: Boolean,
    val coffeeChatStatusSnapshot: String,
    val type: NotificationType,
    val createdAt: LocalDateTime,
    val memberId: Long,
    val memberName: String,
    val memberProfileImageUrl: String?,
    val coffeeChatId: Long,
    val coffeeChatReason: Reason?,
    val coffeeChatReservation: Reservation?,
)
