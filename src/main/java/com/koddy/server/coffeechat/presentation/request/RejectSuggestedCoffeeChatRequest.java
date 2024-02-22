package com.koddy.server.coffeechat.presentation.request;

import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand;
import jakarta.validation.constraints.NotBlank;

public record RejectSuggestedCoffeeChatRequest(
        @NotBlank(message = "거절 사유는 필수입니다.")
        String rejectReason
) {
    public RejectSuggestedCoffeeChatCommand toCommand(
            final long menteeId,
            final long coffeeChatId
    ) {
        return new RejectSuggestedCoffeeChatCommand(
                menteeId,
                coffeeChatId,
                rejectReason
        );
    }
}
