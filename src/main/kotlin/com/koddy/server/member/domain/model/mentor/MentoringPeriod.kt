package com.koddy.server.member.domain.model.mentor

import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.INVALID_TIME_UNIT
import com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Embeddable
data class MentoringPeriod(
    @Column(name = "mentoring_start_date")
    val startDate: LocalDate,

    @Column(name = "mentoring_end_date")
    val endDate: LocalDate,

    @Enumerated(STRING)
    @Column(name = "mentoring_time_unit", nullable = false, columnDefinition = "VARCHAR(20)")
    val timeUnit: TimeUnit = TimeUnit.HALF_HOUR,
) {
    init {
        if (startDate > endDate) {
            throw MemberException(SCHEDULE_PERIOD_TIME_MUST_ALIGN)
        }
    }

    /**
     * start <= ... <= end
     */
    fun isDateIncluded(target: LocalDate): Boolean {
        return target in startDate..endDate
    }

    fun allowedTimeUnit(start: LocalDateTime, end: LocalDateTime): Boolean {
        val requestDuration: Long = ChronoUnit.MINUTES.between(start, end)
        return timeUnit.value.toLong() == requestDuration
    }

    enum class TimeUnit(
        val value: Int,
    ) {
        HALF_HOUR(30),
        ONE_HOUR(60),
        ;

        companion object {
            fun from(value: Int): TimeUnit {
                return entries.firstOrNull { it.value == value }
                    ?: throw MemberException(INVALID_TIME_UNIT)
            }
        }
    }
}
