package com.koddy.server.member.application.usecase.query.response

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.response.MentoringPeriodResponse
import com.koddy.server.member.domain.model.response.ScheduleResponse
import java.time.LocalDateTime

data class MentorReservedSchedule(
    val period: MentoringPeriodResponse?,
    val schedules: List<ScheduleResponse> = emptyList(),
    val timeUnit: Int?,
    val reserved: List<Reserved> = emptyList(),
) {
    companion object {
        fun of(
            mentor: Mentor,
            reservedCoffeeChat: List<CoffeeChat>,
        ): MentorReservedSchedule {
            return MentorReservedSchedule(
                period = MentoringPeriodResponse.from(mentor.mentoringPeriod),
                schedules = mentor.schedules.map { ScheduleResponse.from(it.timeline) },
                timeUnit = mentor.mentoringTimeUnit,
                reserved = reservedCoffeeChat.map { Reserved.from(it.reservation) },
            )
        }
    }
}

data class Reserved(
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    companion object {
        fun from(reservation: Reservation): Reserved {
            return Reserved(
                start = reservation.start,
                end = reservation.end,
            )
        }
    }
}
