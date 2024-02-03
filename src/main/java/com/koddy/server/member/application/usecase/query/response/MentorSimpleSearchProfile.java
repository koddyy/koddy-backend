package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.Mentor;

public record MentorSimpleSearchProfile(
        long id,
        String name,
        String profileImageUrl,
        String school,
        String major,
        int enteredIn
) {
    public static MentorSimpleSearchProfile from(final Mentor mentor) {
        return new MentorSimpleSearchProfile(
                mentor.getId(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getUniversityProfile().getSchool(),
                mentor.getUniversityProfile().getMajor(),
                mentor.getUniversityProfile().getEnteredIn()
        );
    }
}
