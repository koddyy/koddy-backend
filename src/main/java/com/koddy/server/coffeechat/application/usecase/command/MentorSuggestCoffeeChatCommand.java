package com.koddy.server.coffeechat.application.usecase.command;

public record MentorSuggestCoffeeChatCommand(
        long mentorId,
        long menteeId,
        String suggestReason
) {
}
