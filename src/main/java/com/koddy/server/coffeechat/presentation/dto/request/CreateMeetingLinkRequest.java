package com.koddy.server.coffeechat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateMeetingLinkRequest(
        @NotBlank(message = "Authorization Code는 필수입니다.")
        String authorizationCode,

        @NotBlank(message = "Redirect Uri는 필수입니다.")
        String redirectUri,

        @NotBlank(message = "State값은 필수입니다.")
        String state,

        @NotBlank(message = "회의 제목은 필수입니다")
        String topic,

        @NotNull(message = "회의 시작 시간은 필수입니다.")
        LocalDateTime start,

        @NotNull(message = "회의 종료 시간은 필수입니다.")
        LocalDateTime end
) {
}
