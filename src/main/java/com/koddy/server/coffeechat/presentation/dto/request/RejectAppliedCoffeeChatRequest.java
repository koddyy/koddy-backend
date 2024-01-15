package com.koddy.server.coffeechat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectAppliedCoffeeChatRequest(
        @NotBlank(message = "거절 사유는 필수입니다.")
        String rejectReason
) {
}
