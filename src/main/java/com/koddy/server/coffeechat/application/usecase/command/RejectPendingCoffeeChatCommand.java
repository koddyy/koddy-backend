package com.koddy.server.coffeechat.application.usecase.command;

public record RejectPendingCoffeeChatCommand(
        long mentorId,
        long coffeeChatId,
        String rejectReason
) {
}
