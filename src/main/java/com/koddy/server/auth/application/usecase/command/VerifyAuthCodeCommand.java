package com.koddy.server.auth.application.usecase.command;

public record VerifyAuthCodeCommand(
        String email,
        String authCode
) {
}
