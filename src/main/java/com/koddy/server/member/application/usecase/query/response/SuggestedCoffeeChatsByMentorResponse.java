package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.repository.query.response.SuggestedCoffeeChatsByMentor;

public record SuggestedCoffeeChatsByMentorResponse(
        long coffeeChatId,
        long mentorId,
        String name,
        String profileImageUrl,
        String school,
        String major,
        int enteredIn
) {
    public static SuggestedCoffeeChatsByMentorResponse from(final SuggestedCoffeeChatsByMentor result) {
        return new SuggestedCoffeeChatsByMentorResponse(
                result.coffeeChatId(),
                result.mentorId(),
                result.name(),
                result.profileImageUrl(),
                result.universityProfile().getSchool(),
                result.universityProfile().getMajor(),
                result.universityProfile().getEnteredIn()
        );
    }
}
