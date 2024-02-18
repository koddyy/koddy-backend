package com.koddy.server.member.presentation.request;

import jakarta.validation.constraints.NotNull;

public record ReservedScheduleRequest(
        @NotNull(message = "Year 정보는 필수입니다.")
        Integer year,

        @NotNull(message = "Month 정보는 필수입니다.")
        Integer month
) {
}
