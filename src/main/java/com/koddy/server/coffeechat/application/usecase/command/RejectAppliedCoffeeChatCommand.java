package com.koddy.server.coffeechat.application.usecase.command;

public record RejectAppliedCoffeeChatCommand(
        long mentorId,
        long coffeeChatId,
        String rejectReason
) {
}
