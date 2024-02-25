package com.koddy.server.coffeechat.presentation.request;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import jakarta.validation.constraints.NotBlank;

public record CancelCoffeeChatRequest(
        @NotBlank(message = "취소 사유는 필수입니다.")
        String cancelReason
) {
    public CancelCoffeeChatCommand toCommand(
            final Authenticated authenticated,
            final long coffeeChatId
    ) {
        return new CancelCoffeeChatCommand(
                authenticated,
                coffeeChatId,
                cancelReason
        );
    }
}
