package com.koddy.server.coffeechat.presentation.dto.request;

import com.koddy.server.coffeechat.domain.model.Strategy;
import jakarta.validation.constraints.NotBlank;

public record ApproveAppliedCoffeeChatRequest(
        @NotBlank(message = "멘토링 진행 방식은 필수입니다.")
        String chatType,

        @NotBlank(message = "멘토링 진행 방식에 대한 URL이나 메신저 ID는 필수입니다.")
        String chatValue
) {
    public Strategy.Type toStrategyType() {
        return Strategy.Type.from(chatType);
    }
}
