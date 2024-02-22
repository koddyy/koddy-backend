package com.koddy.server.coffeechat.application.usecase.command;

public record CreateCoffeeChatBySuggestCommand(
        long mentorId,
        long menteeId,
        String suggestReason
) {
}
