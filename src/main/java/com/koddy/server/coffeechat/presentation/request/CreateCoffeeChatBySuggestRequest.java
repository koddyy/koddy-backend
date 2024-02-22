package com.koddy.server.coffeechat.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCoffeeChatBySuggestRequest(
        @NotNull(message = "멘티 정보는 필수입니다.")
        Long menteeId,

        @NotBlank(message = "멘티에게 커피챗을 제안하는 이유를 입력해주세요.")
        String suggestReason
) {
}
