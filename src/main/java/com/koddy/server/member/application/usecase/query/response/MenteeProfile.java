package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentee.Mentee;

public record MenteeProfile(
        Long id,
        String email,
        String name,
        String profileImageUrl,
        String nationality,
        String introduction,
        LanguageResponse languages,
        InterestResponse interest
) {
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
                new InterestResponse(mentee.getInterest())
        );
    }
}
