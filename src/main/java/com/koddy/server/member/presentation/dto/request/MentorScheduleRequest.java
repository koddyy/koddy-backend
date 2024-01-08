package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.DayOfWeek;
import com.koddy.server.member.domain.model.mentor.Period;
import com.koddy.server.member.domain.model.mentor.Timeline;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record MentorScheduleRequest(
        String day,
        Start start,
        End end
) {
    public record Start(
            int hour,
            int minute
    ) {
    }

    public record End(
            int hour,
            int minute
    ) {
    }

    public Timeline toSchedule() {
        return new Timeline(
                DayOfWeek.from(day),
                Period.of(
                        LocalTime.of(start.hour, start.minute),
                        LocalTime.of(end.hour, end.minute)
                )
        );
    }
}
