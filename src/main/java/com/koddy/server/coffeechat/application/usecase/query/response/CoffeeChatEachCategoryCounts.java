package com.koddy.server.coffeechat.application.usecase.query.response;

public record CoffeeChatEachCategoryCounts(
        long waiting,
        long suggested,
        long scheduled,
        long passed
) {
}
