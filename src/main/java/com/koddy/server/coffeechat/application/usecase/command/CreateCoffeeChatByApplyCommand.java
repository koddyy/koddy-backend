package com.koddy.server.coffeechat.application.usecase.command;

import com.koddy.server.coffeechat.domain.model.Reservation;

public record CreateCoffeeChatByApplyCommand(
        long menteeId,
        long mentorId,
        String applyReason,
        Reservation reservation
) {
}
