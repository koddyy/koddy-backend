package com.koddy.server.notification.domain.repository.query.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.model.Reason;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record NotificationDetails(
        long id,
        boolean read,
        LocalDateTime createdAt,
        long memberId,
        String memberName,
        String memberProfileImageUrl,
        long coffeeChatId,
        CoffeeChatStatus coffeeChatStatus,
        Reason coffeeChatReason,
        Reservation coffeeChatReservation
) {
    @QueryProjection
    public NotificationDetails {
    }
}
