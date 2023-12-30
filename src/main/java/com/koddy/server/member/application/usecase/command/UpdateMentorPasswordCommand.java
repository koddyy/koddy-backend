package com.koddy.server.member.application.usecase.command;

public record UpdateMentorPasswordCommand(
        Long mentorId,
        String currentPassword,
        String updatePassword
) {
}
