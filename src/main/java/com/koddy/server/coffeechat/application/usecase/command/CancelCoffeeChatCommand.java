package com.koddy.server.coffeechat.application.usecase.command;

import com.koddy.server.auth.domain.model.Authenticated;

public record CancelCoffeeChatCommand(
        Authenticated authenticated,
        long coffeeChatId,
        String cancelReason
) {
}
