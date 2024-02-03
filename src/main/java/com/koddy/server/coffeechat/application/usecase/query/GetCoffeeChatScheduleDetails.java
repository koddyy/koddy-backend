package com.koddy.server.coffeechat.application.usecase.query;

import com.koddy.server.auth.domain.model.Authenticated;

public record GetCoffeeChatScheduleDetails(
        Authenticated authenticated,
        long coffeeChatId
) {
}
