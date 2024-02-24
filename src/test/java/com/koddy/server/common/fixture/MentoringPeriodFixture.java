package com.koddy.server.common.fixture;

import com.koddy.server.member.domain.model.mentor.MentoringPeriod;

import java.time.LocalDate;

public enum MentoringPeriodFixture {
    FROM_01_01_TO_12_31(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)),
    FROM_02_01_TO_12_31(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 12, 31)),
    FROM_03_01_TO_05_01(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 5, 1)),
    ;

    private final LocalDate startDate;
    private final LocalDate endDate;

    MentoringPeriodFixture(
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public MentoringPeriod toDomain() {
        return MentoringPeriod.of(startDate, endDate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
