package com.koddy.server.coffeechat.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.global.utils.TimeUtils;
import jakarta.validation.constraints.NotBlank;

public record PendingSuggestedCoffeeChatRequest(
        @NotBlank(message = "멘토링 신청날짜는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String start,

        @NotBlank(message = "멘토링 신청날짜는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String end
) {
    public Reservation toReservationStart() {
        return new Reservation(TimeUtils.toLocalDateTime(start));
    }

    public Reservation toReservationEnd() {
        return new Reservation(TimeUtils.toLocalDateTime(end));
    }
}
