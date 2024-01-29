package com.koddy.server.coffeechat.domain.repository.query.spec;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;

public record MentorCoffeeChatQueryCondition(
        long mentorId,
        CoffeeChatStatus status
) {
}
