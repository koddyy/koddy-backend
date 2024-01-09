package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentee.Mentee;

import static com.koddy.server.member.domain.model.ProfileComplete.YES;

public record MenteeProfile(
        Long id,
        String email,
        String name,
        String profileImageUrl,
        String nationality,
        String introduction,
        LanguageResponse languages,
        InterestResponse interest,
        String role,
        boolean profileComplete
) implements MemberProfile {
    public record InterestResponse(
            String school,
            String major
    ) {
        public InterestResponse(final Interest interest) {
            this(interest.getSchool(), interest.getMajor());
        }
    }

    public MenteeProfile(final Mentee mentee) {
        this(
                mentee.getId(),
                mentee.getEmail().getValue(),
                mentee.getName(),
                mentee.getProfileImageUrl(),
                mentee.getNationality().getKor(),
                mentee.getIntroduction(),
                new LanguageResponse(mentee.getLanguages()),
                new InterestResponse(mentee.getInterest()),
                "mentee",
                mentee.getProfileComplete() == YES
        );
    }
}
