package com.koddy.server.coffeechat.domain.repository.query.response;

import com.querydsl.core.annotations.QueryProjection;

public record CoffeeChatCountPerCategory(
        long waiting,
        long suggested,
        long scheduled,
        long passed
) {
    @QueryProjection
    public CoffeeChatCountPerCategory {
    }
}
