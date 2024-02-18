package com.koddy.server.coffeechat.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.koddy.server.global.utils.TimeUtils;
import jakarta.validation.constraints.NotBlank;
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

        @NotBlank(message = "회의 시작 시간은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String start,

        @NotBlank(message = "회의 종료 시간은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String end
) {
    public LocalDateTime toStart() {
        return TimeUtils.toLocalDateTime(start);
    }

    public LocalDateTime toEnd() {
        return TimeUtils.toLocalDateTime(end);
    }
}
