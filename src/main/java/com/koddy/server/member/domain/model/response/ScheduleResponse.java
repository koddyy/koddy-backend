package com.koddy.server.member.domain.model.response;

import com.koddy.server.member.domain.model.mentor.Timeline;

public record ScheduleResponse(
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

    public static ScheduleResponse from(final Timeline timeline) {
        return new ScheduleResponse(
                timeline.getDayOfWeek().getKor(),
                new ScheduleResponse.Start(timeline.getStartTime().getHour(), timeline.getStartTime().getMinute()),
                new ScheduleResponse.End(timeline.getEndTime().getHour(), timeline.getEndTime().getMinute())
        );
    }
}
