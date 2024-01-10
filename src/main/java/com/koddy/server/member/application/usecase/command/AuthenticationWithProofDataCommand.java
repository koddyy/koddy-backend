package com.koddy.server.member.application.usecase.command;

public record AuthenticationWithProofDataCommand(
        long mentorId,
        String proofDataUploadUrl
) {
}
