package com.koddy.server.member.application.usecase.query;

public record GetSuggestedMentors(
        long menteeId,
        int limit
) {
}
