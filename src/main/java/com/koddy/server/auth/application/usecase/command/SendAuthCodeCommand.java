package com.koddy.server.auth.application.usecase.command;

public record SendAuthCodeCommand(
        String email
) {
}
