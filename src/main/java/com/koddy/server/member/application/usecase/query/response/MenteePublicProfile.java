package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.response.LanguageResponse;

public record MenteePublicProfile(
        long id,
        String name,
        String profileImageUrl,
        String nationality,
        String introduction,
        LanguageResponse languages,
        String interestSchool,
        String interestMajor
) {
    public static MenteePublicProfile from(final Mentee mentee) {
        return new MenteePublicProfile(
                mentee.getId(),
                mentee.getName(),
                mentee.getProfileImageUrl(),
                mentee.getNationality().code,
                mentee.getIntroduction(),
                LanguageResponse.of(mentee.getLanguages()),
                mentee.getInterest().getSchool(),
                mentee.getInterest().getMajor()
        );
    }
}
