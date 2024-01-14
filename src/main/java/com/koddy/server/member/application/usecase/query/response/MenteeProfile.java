package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentee.Mentee;

import static com.koddy.server.member.domain.model.ProfileComplete.YES;

public record MenteeProfile(
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
) implements MemberProfile {
    public static MenteeProfile of(final Mentee mentee) {
        return new MenteeProfile(
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
                mentee.getProfileComplete() == YES
        );
    }
}
