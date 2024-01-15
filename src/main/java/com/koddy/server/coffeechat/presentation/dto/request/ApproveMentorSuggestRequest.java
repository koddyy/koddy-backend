package com.koddy.server.coffeechat.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ApproveMentorSuggestRequest(
        @NotNull(message = "멘토링 신청날짜는 필수입니다.")
        LocalDateTime start,

        @NotNull(message = "멘토링 신청날짜는 필수입니다.")
        LocalDateTime end
) {
}
