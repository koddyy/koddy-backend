package com.koddy.server.member.application.usecase.command;

public record AuthenticationConfirmWithMailCommand(
        long mentorId,
        String schoolMail,
        String authCode
) {
}
