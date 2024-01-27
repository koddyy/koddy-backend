package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentee.Mentee;

public record MenteeSimpleSearchProfile(
        long id,
        String name,
        String profileImageUrl,
        String nationality,
        String interestSchool,
        String interestMajor
) {
    public static MenteeSimpleSearchProfile of(final Mentee mentee) {
        return new MenteeSimpleSearchProfile(
                mentee.getId(),
                mentee.getName(),
                mentee.getProfileImageUrl(),
                mentee.getNationality().getValue(),
                mentee.getInterest().getSchool(),
                mentee.getInterest().getMajor()
        );
    }
}
