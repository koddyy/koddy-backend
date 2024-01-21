package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.global.utils.TimeUtils;
import com.koddy.server.member.domain.model.mentor.DayOfWeek;
import com.koddy.server.member.domain.model.mentor.Timeline;
import lombok.Builder;

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
                TimeUtils.toLocalTime(start.hour, start.minute),
                TimeUtils.toLocalTime(end.hour, end.minute)
        );
    }
}
