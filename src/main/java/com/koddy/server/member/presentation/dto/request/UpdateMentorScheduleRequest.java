package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.Period;
import com.koddy.server.member.domain.model.mentor.Schedule;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateMentorScheduleRequest(
        @NotEmpty(message = "멘토링 스케줄은 하루 이상 선택해야 합니다.")
        List<MentorScheduleRequest> schedules
) {
    public List<Schedule> toSchedules() {
        return schedules.stream()
                .map(it -> new Schedule(it.day(), Period.of(it.startTime(), it.endTime())))
                .toList();
    }
}
