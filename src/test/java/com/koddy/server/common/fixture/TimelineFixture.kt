package com.koddy.server.common.fixture

import com.koddy.server.member.domain.model.mentor.DayOfWeek
import com.koddy.server.member.domain.model.mentor.Timeline
import java.time.LocalTime

enum class TimelineFixture(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    MON_09_22(DayOfWeek.MON, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    TUE_09_22(DayOfWeek.TUE, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    WED_09_22(DayOfWeek.WED, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    THU_09_22(DayOfWeek.THU, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    FRI_09_22(DayOfWeek.FRI, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    SAT_09_22(DayOfWeek.SAT, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    SUN_09_22(DayOfWeek.SUN, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    ;

    fun toDomain(): Timeline {
        return Timeline(
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            endTime = endTime,
        )
    }

    companion object {
        @JvmStatic
        fun 월_수_금(): List<Timeline> {
            return listOf(
                MON_09_22.toDomain(),
                WED_09_22.toDomain(),
                FRI_09_22.toDomain(),
            )
        }

        @JvmStatic
        fun 화_목_토(): List<Timeline> {
            return listOf(
                TUE_09_22.toDomain(),
                THU_09_22.toDomain(),
                SAT_09_22.toDomain(),
            )
        }

        @JvmStatic
        fun 월_화_수_목_금(): List<Timeline> {
            return listOf(
                MON_09_22.toDomain(),
                TUE_09_22.toDomain(),
                WED_09_22.toDomain(),
                THU_09_22.toDomain(),
                FRI_09_22.toDomain(),
            )
        }

        @JvmStatic
        fun 주말(): List<Timeline> {
            return listOf(
                SAT_09_22.toDomain(),
                SUN_09_22.toDomain(),
            )
        }

        @JvmStatic
        fun allDays(): List<Timeline> {
            return listOf(
                MON_09_22.toDomain(),
                TUE_09_22.toDomain(),
                WED_09_22.toDomain(),
                THU_09_22.toDomain(),
                FRI_09_22.toDomain(),
                SAT_09_22.toDomain(),
                SUN_09_22.toDomain(),
            )
        }
    }
}
