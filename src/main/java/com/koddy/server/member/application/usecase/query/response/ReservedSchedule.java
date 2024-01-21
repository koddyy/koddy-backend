package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.coffeechat.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public record ReservedSchedule(
        int timeUnit,
        List<Period> periods
) {
    public record Period(
            LocalDateTime start,
            LocalDateTime end
    ) {
        public static Period of(final Reservation start, final Reservation end) {
            return new Period(start.toLocalDateTime(), end.toLocalDateTime());
        }
    }
}
