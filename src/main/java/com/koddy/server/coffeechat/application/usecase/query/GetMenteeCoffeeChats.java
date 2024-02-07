package com.koddy.server.coffeechat.application.usecase.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;

import java.util.List;

public record GetMenteeCoffeeChats(
        long menteeId,
        List<CoffeeChatStatus> status,
        int page
) {
}
