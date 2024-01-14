package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import com.koddy.server.member.domain.model.mentor.Timeline;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Builder
public record UpdateMentorScheduleRequest(
        MentoringPeriodRequest period,
        List<MentorScheduleRequest> schedules
) {
    public MentoringPeriod toPeriod() {
        return period.toPeriod();
    }

    public List<Timeline> toSchedules() {
        if (CollectionUtils.isEmpty(schedules)) {
            return List.of();
        }
        return schedules.stream()
                .map(MentorScheduleRequest::toTimeline)
                .toList();
    }
}
