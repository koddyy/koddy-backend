package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.response.MentoringPeriodResponse;
import com.koddy.server.member.domain.model.response.ScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

public record MentorReservedSchedule(
        MentoringPeriodResponse period,
        List<ScheduleResponse> schedules,
        Integer timeUnit,
        List<Reserved> reserved
) {
    public record Reserved(
            LocalDateTime start,
            LocalDateTime end
    ) {
        public static Reserved from(final Reservation reservation) {
            return new Reserved(reservation.getStart(), reservation.getEnd());
        }
    }

    public static MentorReservedSchedule of(final Mentor mentor, final List<CoffeeChat> reservedCoffeeChat) {
        return new MentorReservedSchedule(
                MentoringPeriodResponse.from(mentor.getMentoringPeriod()),
                mentor.getSchedules()
                        .stream()
                        .map(it -> ScheduleResponse.from(it.getTimeline()))
                        .toList(),
                mentor.getMentoringTimeUnit(),
                reservedCoffeeChat.stream()
                        .map(it -> MentorReservedSchedule.Reserved.from(it.getReservation()))
                        .toList()
        );
    }
}
