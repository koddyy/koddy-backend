package com.koddy.server.coffeechat.application.usecase.command;

public record RejectAppliedCoffeeChatCommand(
        long coffeeChatId,
        String rejectReason
) {
}
