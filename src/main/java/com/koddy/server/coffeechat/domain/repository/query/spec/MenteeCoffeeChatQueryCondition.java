package com.koddy.server.coffeechat.domain.repository.query.spec;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;

public record MenteeCoffeeChatQueryCondition(
        long menteeId,
        CoffeeChatStatus status
) {
}
