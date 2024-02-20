package com.koddy.server.coffeechat.presentation.request;

import com.koddy.server.coffeechat.application.usecase.command.RejectAppliedCoffeeChatCommand;
import jakarta.validation.constraints.NotBlank;

public record RejectAppliedCoffeeChatRequest(
        @NotBlank(message = "거절 사유는 필수입니다.")
        String rejectReason
) {
    public RejectAppliedCoffeeChatCommand toCommand(
            final long mentorId,
            final long coffeeChatId
    ) {
        return new RejectAppliedCoffeeChatCommand(
                mentorId,
                coffeeChatId,
                rejectReason
        );
    }
}
