package com.koddy.server.member.presentation.request

import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import java.time.LocalDate

data class MentoringPeriodRequest(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
) {
    fun toPeriod(): MentoringPeriod? {
        if (startDate != null && endDate != null) {
            return MentoringPeriod.of(startDate, endDate)
        }
        return null
    }
}
