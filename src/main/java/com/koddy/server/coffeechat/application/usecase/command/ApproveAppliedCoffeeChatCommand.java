package com.koddy.server.coffeechat.application.usecase.command;

import com.koddy.server.coffeechat.domain.model.Strategy;

public record ApproveAppliedCoffeeChatCommand(
        long mentorId,
        long coffeeChatId,
        Strategy.Type type,
        String value
) {
}
