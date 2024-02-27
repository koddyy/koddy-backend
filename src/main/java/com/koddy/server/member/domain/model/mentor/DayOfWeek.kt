package com.koddy.server.member.domain.model.mentor

import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.INVALID_DAY
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalDate

enum class DayOfWeek(
    val kor: String,
    val eng: String,
) {
    MON("월", "MON"),
    TUE("화", "TUE"),
    WED("수", "WED"),
    THU("목", "THU"),
    FRI("금", "FRI"),
    SAT("토", "SAT"),
    SUN("일", "SUN"),
    ;

    companion object {
        @JvmStatic
        fun from(kor: String): DayOfWeek =
            entries.firstOrNull { it.kor == kor }
                ?: throw MemberException(INVALID_DAY)

        fun of(kors: List<String>): List<DayOfWeek> = kors.map { from(it) }

        @JvmStatic
        fun of(year: Int, month: Int, day: Int): DayOfWeek =
            when (LocalDate.of(year, month, day).dayOfWeek!!) {
                MONDAY -> MON
                TUESDAY -> TUE
                WEDNESDAY -> WED
                THURSDAY -> THU
                FRIDAY -> FRI
                SATURDAY -> SAT
                SUNDAY -> SUN
            }
    }
}
