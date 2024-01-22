package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.MentoringPeriod;

import java.time.LocalDate;

public record MentoringPeriodResponse(
        LocalDate startDate,
        LocalDate endDate
) {
    public static MentoringPeriodResponse of(final MentoringPeriod mentoringPeriod) {
        if (mentoringPeriod == null) {
            return null;
        }
        return new MentoringPeriodResponse(mentoringPeriod.getStartDate(), mentoringPeriod.getEndDate());
    }
}
