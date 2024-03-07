package com.koddy.server.common.fixture

import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import java.time.LocalDate

enum class MentoringPeriodFixture(
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    FROM_01_01_TO_12_31(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)),
    FROM_02_01_TO_12_31(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 12, 31)),
    FROM_03_01_TO_05_01(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 5, 1)),
    ;

    fun toDomain(): MentoringPeriod {
        return MentoringPeriod(startDate = startDate, endDate = endDate)
    }
}
