package com.koddy.server.coffeechat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MenteeApplyCoffeeChatRequest(
        @NotBlank(message = "멘토에게 커피챗을 신청하는 이유를 입력해주세요.")
        String applyReason,

        @NotNull(message = "커피챗 신청 날짜를 선택해주세요.")
        LocalDateTime start,

        @NotNull(message = "커피챗 신청 날짜를 선택해주세요.")
        LocalDateTime end
) {
}
