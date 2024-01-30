package com.koddy.server.coffeechat.domain.repository.query.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.querydsl.core.annotations.QueryProjection;

public record MentorCoffeeChatScheduleData(
        long id,
        String status,
        long menteeId,
        String name,
        String profileImageUrl,
        String interestSchool,
        String interestMajor
) {
    @QueryProjection
    public MentorCoffeeChatScheduleData(
            final long id,
            final CoffeeChatStatus status,
            final long menteeId,
            final String name,
            final String profileImageUrl,
            final Interest interest
    ) {
        this(
                id,
                status.getValue(),
                menteeId,
                name,
                profileImageUrl,
                interest.getSchool(),
                interest.getMajor()
        );
    }
}
