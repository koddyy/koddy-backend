package com.koddy.server.coffeechat.application.usecase.command;

public record RejectSuggestedCoffeeChatCommand(
        long coffeeChatId,
        String rejectReason
) {
}
