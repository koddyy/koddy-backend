package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.MentoringSetting;

import java.time.LocalDate;

public record MentoringPeriodRequest(
        LocalDate startDate,
        LocalDate endDate
) {
    public MentoringSetting toPeriod() {
        if (startDate != null && endDate != null) {
            return MentoringSetting.of(startDate, endDate);
        }
        return null;
    }
}
