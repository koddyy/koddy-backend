package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.response.LanguageResponse;

public record MentorPublicProfile(
        long id,
        String name,
        String profileImageUrl,
        String introduction,
        LanguageResponse languages,
        String school,
        String major,
        int enteredIn,
        boolean authenticated
) {
    public static MentorPublicProfile from(final Mentor mentor) {
        return new MentorPublicProfile(
                mentor.getId(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getIntroduction(),
                LanguageResponse.of(mentor.getLanguages()),
                mentor.getUniversityProfile().getSchool(),
                mentor.getUniversityProfile().getMajor(),
                mentor.getUniversityProfile().getEnteredIn(),
                mentor.isAuthenticated()
        );
    }
}
