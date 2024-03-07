package com.koddy.server.member.presentation.request.model

import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import java.time.LocalDate

data class MentoringPeriodRequestModel(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
) {
    fun toPeriod(): MentoringPeriod? {
        if (startDate != null && endDate != null) {
            return MentoringPeriod(startDate = startDate, endDate = endDate)
        }
        return null
    }
}
