package com.koddy.server.coffeechat.application.usecase.command;

public record FinallyCancelPendingCoffeeChatCommand(
        long mentorId,
        long coffeeChatId,
        String cancelReason
) {
}
