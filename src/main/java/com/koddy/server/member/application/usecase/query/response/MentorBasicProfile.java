package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.Mentor;

public record MentorBasicProfile(
        long id,
        String name,
        String profileImageurl,
        String introduction,
        LanguageResponse languages,
        String school,
        String major,
        int enteredIn
) {
    public static MentorBasicProfile of(final Mentor mentor) {
        return new MentorBasicProfile(
                mentor.getId(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getIntroduction(),
                LanguageResponse.of(mentor.getLanguages()),
                mentor.getUniversityProfile().getSchool(),
                mentor.getUniversityProfile().getMajor(),
                mentor.getUniversityProfile().getEnteredIn()
        );
    }
}
