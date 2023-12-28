package com.koddy.server.auth.application.usecase.command;

public record ReissueTokenCommand(
        String refreshToken
) {
}
