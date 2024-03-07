package com.koddy.server.member.domain.model.mentor

import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import java.time.LocalTime

@Embeddable
data class Timeline(
    @Enumerated(STRING)
    @Column(name = "day_of_week", nullable = false, columnDefinition = "VARCHAR(20)")
    val dayOfWeek: DayOfWeek,

    @Column(name = "start_time", nullable = false)
    val startTime: LocalTime,

    @Column(name = "end_time", nullable = false)
    val endTime: LocalTime,
) {
    init {
        if (startTime >= endTime) {
            throw MemberException(SCHEDULE_PERIOD_TIME_MUST_ALIGN)
        }
    }

    /**
     * start <= ... <= end
     */
    fun isTimeIncluded(target: LocalTime): Boolean {
        return target in startTime..endTime
    }
}
