package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.Day;
import com.koddy.server.member.domain.model.mentor.Period;
import com.koddy.server.member.domain.model.mentor.Schedule;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateMentorScheduleRequest(
        List<MentorScheduleRequest> schedules
) {
    public List<Schedule> toSchedules() {
        if (schedules.isEmpty()) {
            return List.of();
        }
        return schedules.stream()
                .map(it -> new Schedule(Day.from(it.day()), Period.of(it.startTime(), it.endTime())))
                .toList();
    }
}
