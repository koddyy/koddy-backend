package com.koddy.server.coffeechat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ApprovePendingCoffeeChatRequest(
        @NotBlank(message = "멘토링 진행 방식은 필수입니다.")
        String chatType,

        @NotBlank(message = "멘토링 진행 방식에 대한 URL이나 메신저 ID는 필수입니다.")
        String chatValue
) {
}
