package com.koddy.server.auth.application.usecase.command;

public record ConfirmAuthCodeCommand(
        Long memberId,
        String authCode
) {
}
