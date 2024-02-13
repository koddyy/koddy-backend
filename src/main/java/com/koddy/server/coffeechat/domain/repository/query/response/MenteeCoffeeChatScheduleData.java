package com.koddy.server.coffeechat.domain.repository.query.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import com.querydsl.core.annotations.QueryProjection;

public record MenteeCoffeeChatScheduleData(
        long id,
        String status,
        long mentorId,
        String name,
        String profileImageUrl,
        String school,
        String major,
        int enteredIn
) {
    @QueryProjection
    public MenteeCoffeeChatScheduleData(
            final long id,
            final CoffeeChatStatus status,
            final long mentorId,
            final String name,
            final String profileImageUrl,
            final UniversityProfile universityProfile
    ) {
        this(
                id,
                status.name(),
                mentorId,
                name,
                profileImageUrl,
                universityProfile.getSchool(),
                universityProfile.getMajor(),
                universityProfile.getEnteredIn()
        );
    }
}
