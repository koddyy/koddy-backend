package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.DayOfWeek;
import com.koddy.server.member.domain.model.mentor.Timeline;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record MentorScheduleRequest(
        String dayOfWeek,
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

    public Timeline toTimeline() {
        return Timeline.of(
                DayOfWeek.from(dayOfWeek),
                LocalTime.of(start.hour, start.minute),
                LocalTime.of(end.hour, end.minute)
        );
    }
}
