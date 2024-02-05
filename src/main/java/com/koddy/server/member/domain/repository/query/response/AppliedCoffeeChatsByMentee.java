package com.koddy.server.member.domain.repository.query.response;

import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.querydsl.core.annotations.QueryProjection;

public record AppliedCoffeeChatsByMentee(
        long coffeeChatId,
        long menteeId,
        String name,
        String profileImageUrl,
        Nationality nationality,
        Interest interest
) {
    @QueryProjection
    public AppliedCoffeeChatsByMentee {
    }
}
