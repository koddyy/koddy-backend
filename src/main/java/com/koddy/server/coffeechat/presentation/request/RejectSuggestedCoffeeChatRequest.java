package com.koddy.server.coffeechat.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record RejectSuggestedCoffeeChatRequest(
        @NotBlank(message = "거절 사유는 필수입니다.")
        String rejectReason
) {
}