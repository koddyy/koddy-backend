package com.koddy.server.coffeechat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MentorSuggestCoffeeChatRequest(
        @NotBlank(message = "멘티에게 커피챗을 제안하는 이유를 입력해주세요.")
        String applyReason
) {
}
