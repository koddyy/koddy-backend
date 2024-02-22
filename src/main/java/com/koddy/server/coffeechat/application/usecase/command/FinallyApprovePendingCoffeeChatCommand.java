package com.koddy.server.coffeechat.application.usecase.command;

import com.koddy.server.coffeechat.domain.model.Strategy;

public record FinallyApprovePendingCoffeeChatCommand(
        long mentorId,
        long coffeeChatId,
        Strategy.Type type,
        String value
) {
}