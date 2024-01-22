package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentee.Mentee;

public record MenteePrivateProfile(
        long id,
        String email,
        String name,
        String profileImageUrl,
        String nationality,
        String introduction,
        LanguageResponse languages,
        String interestSchool,
        String interestMajor,
        String role,
        boolean profileComplete
) implements MemberPrivateProfile {
    public static MenteePrivateProfile of(final Mentee mentee) {
        return new MenteePrivateProfile(
                mentee.getId(),
                mentee.getEmail().getValue(),
                mentee.getName(),
                mentee.getProfileImageUrl(),
                mentee.getNationality().getKor(),
                mentee.getIntroduction(),
                LanguageResponse.of(mentee.getLanguages()),
                mentee.getInterest().getSchool(),
                mentee.getInterest().getMajor(),
                "mentee",
                mentee.isProfileComplete()
        );
    }
}
