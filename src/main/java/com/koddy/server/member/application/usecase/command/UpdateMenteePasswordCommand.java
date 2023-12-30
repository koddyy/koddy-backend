package com.koddy.server.member.application.usecase.command;

public record UpdateMenteePasswordCommand(
        Long menteeId,
        String currentPassword,
        String updatePassword
) {
}
