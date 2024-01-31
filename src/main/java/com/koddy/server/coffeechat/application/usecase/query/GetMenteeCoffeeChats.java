package com.koddy.server.coffeechat.application.usecase.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;

import java.util.List;

public record GetMenteeCoffeeChats(
        long menteeId,
        List<CoffeeChatStatus> status,
        int page
) {
    public MenteeCoffeeChatQueryCondition toCondition() {
        return new MenteeCoffeeChatQueryCondition(menteeId, status);
    }
}
