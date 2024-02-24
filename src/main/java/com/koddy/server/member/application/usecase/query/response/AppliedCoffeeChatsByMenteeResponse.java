package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.repository.query.response.AppliedCoffeeChatsByMentee;

public record AppliedCoffeeChatsByMenteeResponse(
        long coffeeChatId,
        long menteeId,
        String name,
        String profileImageUrl,
        String nationality,
        String interestSchool,
        String interestMajor
) {
    public static AppliedCoffeeChatsByMenteeResponse from(final AppliedCoffeeChatsByMentee result) {
        return new AppliedCoffeeChatsByMenteeResponse(
                result.coffeeChatId(),
                result.menteeId(),
                result.name(),
                result.profileImageUrl(),
                result.nationality().code,
                result.interest().getSchool(),
                result.interest().getMajor()
        );
    }
}
