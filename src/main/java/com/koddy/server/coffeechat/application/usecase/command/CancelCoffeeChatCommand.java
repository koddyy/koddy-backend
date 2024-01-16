package com.koddy.server.coffeechat.application.usecase.command;

public record CancelCoffeeChatCommand(
        long memberId,
        long coffeeChatId
) {
}
