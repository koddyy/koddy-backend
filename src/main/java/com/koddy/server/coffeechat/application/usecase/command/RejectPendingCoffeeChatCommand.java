package com.koddy.server.coffeechat.application.usecase.command;

public record RejectPendingCoffeeChatCommand(
        long coffeeChatId,
        String rejectReason
) {
}
