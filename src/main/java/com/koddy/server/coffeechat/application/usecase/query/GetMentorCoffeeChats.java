package com.koddy.server.coffeechat.application.usecase.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;

import java.util.List;

public record GetMentorCoffeeChats(
        long mentorId,
        List<CoffeeChatStatus> status,
        int page
) {
}
