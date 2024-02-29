package com.koddy.server.member.domain.model.response

import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import java.time.LocalDate

data class MentoringPeriodResponse(
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    companion object {
        fun from(mentoringPeriod: MentoringPeriod?): MentoringPeriodResponse? {
            if (mentoringPeriod == null) {
                return null
            }

            return MentoringPeriodResponse(
                startDate = mentoringPeriod.startDate,
                endDate = mentoringPeriod.endDate,
            )
        }
    }
}
