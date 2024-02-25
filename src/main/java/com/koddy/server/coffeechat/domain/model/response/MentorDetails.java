package com.koddy.server.coffeechat.domain.model.response;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.response.LanguageResponse;

import java.util.List;

public record MentorDetails(
        long id,
        String name,
        String profileImageUrl,
        String introduction,
        LanguageResponse languages,
        String school,
        String major,
        int enteredIn,
        String status
) {
    public static MentorDetails from(final Mentor mentor) {
        return new MentorDetails(
                mentor.getId(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getIntroduction(),
                LanguageResponse.of(mentor.getLanguages()),
                mentor.getUniversityProfile().getSchool(),
                mentor.getUniversityProfile().getMajor(),
                mentor.getUniversityProfile().getEnteredIn(),
                mentor.getStatus().name()
        );
    }

    public static MentorDetails of(
            final Mentor mentor,
            final List<Language> languages
    ) {
        return new MentorDetails(
                mentor.getId(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getIntroduction(),
                LanguageResponse.of(languages),
                mentor.getUniversityProfile().getSchool(),
                mentor.getUniversityProfile().getMajor(),
                mentor.getUniversityProfile().getEnteredIn(),
                mentor.getStatus().name()
        );
    }
}
