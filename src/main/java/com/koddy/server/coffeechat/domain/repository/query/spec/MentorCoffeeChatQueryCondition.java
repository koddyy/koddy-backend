package com.koddy.server.coffeechat.domain.repository.query.spec;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;

import java.util.List;

public record MentorCoffeeChatQueryCondition(
        long mentorId,
        List<CoffeeChatStatus> status
) {
}
