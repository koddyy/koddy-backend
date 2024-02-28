package com.koddy.server.coffeechat.domain.service

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.coffeechat.domain.repository.query.MentorReservedScheduleQueryRepository
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION
import org.springframework.stereotype.Component

@Component
class ReservationAvailabilityChecker(
    private val mentorReservedScheduleQueryRepository: MentorReservedScheduleQueryRepository,
) {
    fun check(
        mentor: Mentor,
        reservation: Reservation,
    ) {
        mentor.validateReservationData(reservation)
        validateAlreadyReservedTime(mentor, reservation)
    }

    private fun validateAlreadyReservedTime(
        mentor: Mentor,
        reservation: Reservation,
    ) {
        val reservedCoffeeChat: List<CoffeeChat> = mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(
            mentor.id,
            reservation.start.year,
            reservation.start.monthValue,
        )
        if (alreadyReserved(reservedCoffeeChat, reservation)) {
            throw MemberException(CANNOT_RESERVATION)
        }
    }

    private fun alreadyReserved(
        reservedCoffeeChat: List<CoffeeChat>,
        reservation: Reservation,
    ): Boolean = reservedCoffeeChat.any { it.isRequestReservationIncludedSchedules(reservation) }
}
