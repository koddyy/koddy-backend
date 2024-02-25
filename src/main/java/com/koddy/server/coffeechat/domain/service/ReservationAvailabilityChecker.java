package com.koddy.server.coffeechat.domain.service;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.query.MentorReservedScheduleQueryRepository;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.exception.MemberException;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION;

@Component
public class ReservationAvailabilityChecker {
    private final MentorReservedScheduleQueryRepository mentorReservedScheduleQueryRepository;

    public ReservationAvailabilityChecker(final MentorReservedScheduleQueryRepository mentorReservedScheduleQueryRepository) {
        this.mentorReservedScheduleQueryRepository = mentorReservedScheduleQueryRepository;
    }

    public void check(final Mentor mentor, final Reservation reservation) {
        mentor.validateReservationData(reservation);
        validateAlreadyReservedTime(mentor, reservation);
    }

    private void validateAlreadyReservedTime(final Mentor mentor, final Reservation reservation) {
        final List<CoffeeChat> reservedCoffeeChat = mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(
                mentor.getId(),
                reservation.getStart().getYear(),
                reservation.getStart().getMonthValue()
        );
        if (alreadyReserved(reservedCoffeeChat, reservation)) {
            throw new MemberException(CANNOT_RESERVATION);
        }
    }

    private boolean alreadyReserved(final List<CoffeeChat> reservedCoffeeChat, final Reservation reservation) {
        return reservedCoffeeChat.stream()
                .anyMatch(it -> it.isRequestReservationIncludedSchedules(reservation));
    }
}
