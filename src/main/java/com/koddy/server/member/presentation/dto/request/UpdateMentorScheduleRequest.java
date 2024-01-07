package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.DayOfWeek;
import com.koddy.server.member.domain.model.mentor.Period;
import com.koddy.server.member.domain.model.mentor.Schedule;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Builder
public record UpdateMentorScheduleRequest(
        List<MentorScheduleRequest> schedules
) {
    public List<Schedule> toSchedules() {
        if (CollectionUtils.isEmpty(schedules)) {
            return List.of();
        }
        return schedules.stream()
                .map(it -> new Schedule(DayOfWeek.from(it.day()), Period.of(it.startTime(), it.endTime())))
                .toList();
    }
}
