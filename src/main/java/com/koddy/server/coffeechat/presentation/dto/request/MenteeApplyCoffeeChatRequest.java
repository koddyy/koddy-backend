package com.koddy.server.coffeechat.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.global.utils.TimeUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenteeApplyCoffeeChatRequest(
        @NotNull(message = "멘토 정보는 필수입니다.")
        Long mentorId,

        @NotBlank(message = "멘토에게 커피챗을 신청하는 이유를 입력해주세요.")
        String applyReason,

        @NotBlank(message = "커피챗 신청 날짜를 선택해주세요.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String start,

        @NotBlank(message = "커피챗 신청 날짜를 선택해주세요.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        String end
) {
    public Reservation toReservation() {
        return Reservation.of(TimeUtils.toLocalDateTime(start), TimeUtils.toLocalDateTime(end));
    }
}
