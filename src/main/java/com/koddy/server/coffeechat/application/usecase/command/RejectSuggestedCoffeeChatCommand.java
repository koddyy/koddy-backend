package com.koddy.server.coffeechat.application.usecase.command;

public record RejectSuggestedCoffeeChatCommand(
        long menteeId,
        long coffeeChatId,
        String rejectReason
) {
}
