package com.koddy.server.coffeechat.application.usecase.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;

import java.util.List;

public record GetMentorCoffeeChats(
        long mentorId,
        List<CoffeeChatStatus> status,
        int page
) {
    public MentorCoffeeChatQueryCondition toCondition() {
        return new MentorCoffeeChatQueryCondition(mentorId, status);
    }
}
