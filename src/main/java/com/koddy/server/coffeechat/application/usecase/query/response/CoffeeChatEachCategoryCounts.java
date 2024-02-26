package com.koddy.server.coffeechat.application.usecase.query.response;

import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory;

public record CoffeeChatEachCategoryCounts(
        long waiting,
        long suggested,
        long scheduled,
        long passed
) {
    public static CoffeeChatEachCategoryCounts from(final CoffeeChatCountPerCategory query) {
        return new CoffeeChatEachCategoryCounts(
                query.waiting(),
                query.suggested(),
                query.scheduled(),
                query.passed()
        );
    }
}
