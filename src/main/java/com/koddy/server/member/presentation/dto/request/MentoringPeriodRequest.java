package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.MentoringPeriod;

import java.time.LocalDate;

public record MentoringPeriodRequest(
        LocalDate startDate,
        LocalDate endDate
) {
    public MentoringPeriod toPeriod() {
        if (startDate != null && endDate != null) {
            return MentoringPeriod.of(startDate, endDate);
        }
        return null;
    }
}
