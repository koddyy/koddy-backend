package com.koddy.server.member.presentation.dto.request;

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
                .map(MentorScheduleRequest::toSchedule)
                .toList();
    }
}
