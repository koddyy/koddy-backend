package com.koddy.server.auth.application.usecase.command;

public record ConfirmAuthCodeCommand(
        String email,
        String authCode
) {
}
