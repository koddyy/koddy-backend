package com.koddy.server.coffeechat.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record FinallyCancelPendingCoffeeChatRequest(
        @NotBlank(message = "취소 사유는 필수입니다.")
        String cancelReason
) {
}
