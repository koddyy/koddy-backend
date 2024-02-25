package com.koddy.server.notification.domain.repository.query.response;

import com.koddy.server.coffeechat.domain.model.Reason;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.notification.domain.model.NotificationType;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record NotificationDetails(
        long id,
        boolean read,
        NotificationType type,
        LocalDateTime createdAt,
        long memberId,
        String memberName,
        String memberProfileImageUrl,
        long coffeeChatId,
        Reason coffeeChatReason,
        Reservation coffeeChatReservation
) {
    @QueryProjection
    public NotificationDetails {
    }
}
