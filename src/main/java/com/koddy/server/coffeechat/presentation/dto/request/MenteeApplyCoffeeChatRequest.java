package com.koddy.server.coffeechat.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.global.utils.TimeUtils;
import jakarta.validation.constraints.NotBlank;

public record MenteeApplyCoffeeChatRequest(
        @NotBlank(message = "멘토에게 커피챗을 신청하는 이유를 입력해주세요.")
        String applyReason,

        @NotBlank(message = "커피챗 신청 날짜를 선택해주세요.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String start,

        @NotBlank(message = "커피챗 신청 날짜를 선택해주세요.")
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
