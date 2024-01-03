package com.koddy.server.member.presentation.dto.request;

import lombok.Builder;

import java.time.LocalTime;

@Builder
public record MentorScheduleRequest(
        String day,
        LocalTime startTime,
        LocalTime endTime
) {
}
