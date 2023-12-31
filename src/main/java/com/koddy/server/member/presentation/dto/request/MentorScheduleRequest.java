package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.Day;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record MentorScheduleRequest(
        Day day,
        LocalTime startTime,
        LocalTime endTime
) {
}
