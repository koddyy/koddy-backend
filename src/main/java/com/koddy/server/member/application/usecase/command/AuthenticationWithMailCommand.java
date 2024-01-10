package com.koddy.server.member.application.usecase.command;

public record AuthenticationWithMailCommand(
        long mentorId,
        String schoolMail
) {
}
