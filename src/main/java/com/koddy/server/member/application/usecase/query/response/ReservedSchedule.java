package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.response.MentoringPeriodResponse;
import com.koddy.server.member.domain.model.response.ScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ReservedSchedule(
        MentoringPeriodResponse period,
        List<ScheduleResponse> schedules,
        Integer timeUnit,
        List<Reserved> reserved
) {
    public record Reserved(
            LocalDateTime start,
            LocalDateTime end
    ) {
        public static Reserved of(final Reservation start, final Reservation end) {
            return new Reserved(start.toLocalDateTime(), end.toLocalDateTime());
        }
    }

    public static ReservedSchedule of(final Mentor mentor, final List<CoffeeChat> reservedCoffeeChat) {
        return new ReservedSchedule(
                MentoringPeriodResponse.from(mentor.getMentoringPeriod()),
                mentor.getSchedules()
                        .stream()
                        .map(it -> ScheduleResponse.from(it.getTimeline()))
                        .toList(),
                mentor.getMentoringTimeUnit(),
                reservedCoffeeChat.stream()
                        .map(it -> ReservedSchedule.Reserved.of(it.getStart(), it.getEnd()))
                        .toList()
        );
    }
}
