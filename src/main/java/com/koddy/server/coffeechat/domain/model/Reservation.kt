package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_MUST_ALIGN
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class Reservation(
    @Column(name = "start")
    val start: LocalDateTime,

    @Column(name = "end")
    val end: LocalDateTime,
) {
    init {
        if (start > end) {
            throw CoffeeChatException(RESERVATION_MUST_ALIGN)
        }
    }

    /**
     * - target's start가 예약된 시간에 포함 = (start <= target.start) && (target.start < end) && (end < target.end)
     * - target's end가 예약된 시간에 포함 = (target.start < start) && (start < target.end) && (target.end <= end)
     * - target's start & end가 예약된 시간에 포함 = (target.start in start..end) && (target.end in start..end)
     * - target's start & end가 예약된 시간을 전체 커버 = (target.start <= start) && (end <= target.end)
     */
    fun isDateTimeIncluded(target: Reservation): Boolean {
        return when {
            isStartInReservationRange(target) -> true
            isEndInReservationRange(target) -> true
            isStartEndInReservationRange(target) -> true
            isStartEndAllCoverReservationRange(target) -> true
            else -> false
        }
    }

    private fun isStartInReservationRange(target: Reservation): Boolean =
        (this.start <= target.start) && (target.start < this.end) && (this.end < target.end)

    private fun isEndInReservationRange(target: Reservation): Boolean =
        (target.start < this.start) && (this.start < target.end) && (target.end <= this.end)

    private fun isStartEndInReservationRange(target: Reservation): Boolean =
        (target.start in this.start..this.end) && (target.end in this.start..this.end)

    private fun isStartEndAllCoverReservationRange(target: Reservation): Boolean =
        (target.start <= this.start) && (this.end <= target.end)
}
