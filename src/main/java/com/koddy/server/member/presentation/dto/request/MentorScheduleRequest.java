package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.mentor.Day;

import java.time.LocalTime;

public record MentorScheduleRequest(
        Day day,
        LocalTime startTime,
        LocalTime endTime
) {
}
