package com.koddy.server.member.application.usecase.query;

public record GetAppliedMentees(
        long mentorId,
        int limit
) {
}
