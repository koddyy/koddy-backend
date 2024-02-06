package com.koddy.server.member.domain.repository.query.response;

import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import com.querydsl.core.annotations.QueryProjection;

public record SuggestedCoffeeChatsByMentor(
        long coffeeChatId,
        long mentorId,
        String name,
        String profileImageUrl,
        UniversityProfile universityProfile
) {
    @QueryProjection
    public SuggestedCoffeeChatsByMentor {
    }
}
