package com.koddy.server.coffeechat.domain.model.response;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.response.LanguageResponse;

import java.util.List;

public record MenteeDetails(
        long id,
        String name,
        String profileImageUrl,
        String nationality,
        String introduction,
        LanguageResponse languages,
        String interestSchool,
        String interestMajor,
        String status
) {
    public static MenteeDetails from(final Mentee mentee) {
        return new MenteeDetails(
                mentee.getId(),
                mentee.getName(),
                mentee.getProfileImageUrl(),
                mentee.getNationality().code,
                mentee.getIntroduction(),
                LanguageResponse.of(mentee.getLanguages()),
                mentee.getInterest().getSchool(),
                mentee.getInterest().getMajor(),
                mentee.getStatus().name()
        );
    }

    public static MenteeDetails of(
            final Mentee mentee,
            final List<Language> languages
    ) {
        return new MenteeDetails(
                mentee.getId(),
                mentee.getName(),
                mentee.getProfileImageUrl(),
                mentee.getNationality().code,
                mentee.getIntroduction(),
                LanguageResponse.of(languages),
                mentee.getInterest().getSchool(),
                mentee.getInterest().getMajor(),
                mentee.getStatus().name()
        );
    }
}
