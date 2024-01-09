package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.Timeline;
import org.springframework.util.CollectionUtils;

import java.util.List;

public record CompleteMentorProfileRequest(
        String introduction,
        List<MentorScheduleRequest> schedules
) {
    public List<Timeline> toSchedules() {
        if (CollectionUtils.isEmpty(schedules)) {
            return List.of();
        }
        return schedules.stream()
                .map(MentorScheduleRequest::toSchedule)
                .toList();
    }
}
