package com.koddy.server.member.application.usecase.command;

public record CompleteMenteeProfileCommand(
        long menteeId,
        String introduction
) {
}
