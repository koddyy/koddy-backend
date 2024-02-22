package com.koddy.server.coffeechat.presentation.request;

import com.koddy.server.coffeechat.application.usecase.command.FinallyCancelPendingCoffeeChatCommand;
import jakarta.validation.constraints.NotBlank;

public record FinallyCancelPendingCoffeeChatRequest(
        @NotBlank(message = "취소 사유는 필수입니다.")
        String cancelReason
) {
    public FinallyCancelPendingCoffeeChatCommand toCommand(
            final long mentorId,
            final long coffeeChatId
    ) {
        return new FinallyCancelPendingCoffeeChatCommand(
                mentorId,
                coffeeChatId,
                cancelReason
        );
    }
}
