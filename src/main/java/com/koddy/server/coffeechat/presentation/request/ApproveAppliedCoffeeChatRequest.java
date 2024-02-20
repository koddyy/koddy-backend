package com.koddy.server.coffeechat.presentation.request;

import com.koddy.server.coffeechat.application.usecase.command.ApproveAppliedCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.Strategy;
import jakarta.validation.constraints.NotBlank;

public record ApproveAppliedCoffeeChatRequest(
        @NotBlank(message = "멘티에게 궁금한 점은 필수입니다.")
        String question,

        @NotBlank(message = "멘토링 진행 방식은 필수입니다.")
        String chatType,

        @NotBlank(message = "멘토링 진행 방식에 대한 URL이나 메신저 ID는 필수입니다.")
        String chatValue
) {
    public ApproveAppliedCoffeeChatCommand toCommand(
            final long mentorId,
            final long coffeeChatId
    ) {
        return new ApproveAppliedCoffeeChatCommand(
                mentorId,
                coffeeChatId,
                question,
                Strategy.Type.from(chatType),
                chatValue
        );
    }
}
