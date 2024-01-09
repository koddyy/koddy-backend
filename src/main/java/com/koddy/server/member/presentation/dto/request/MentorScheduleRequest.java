package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.DayOfWeek;
import com.koddy.server.member.domain.model.mentor.Period;
import com.koddy.server.member.domain.model.mentor.Timeline;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record MentorScheduleRequest(
        LocalDate startDate,
        LocalDate endDate,
        String dayOfWeek,
        Start startTime,
        End endTime
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
                startDate, endDate,
                DayOfWeek.from(dayOfWeek),
                Period.of(
                        LocalTime.of(startTime.hour, startTime.minute),
                        LocalTime.of(endTime.hour, endTime.minute)
                )
        );
    }
}
