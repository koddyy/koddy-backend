package com.koddy.server.coffeechat.application.usecase.command;

import com.koddy.server.coffeechat.domain.model.Reservation;

public record PendingSuggestedCoffeeChatCommand(
        long coffeeChatId,
        Reservation start,
        Reservation end
) {
}
