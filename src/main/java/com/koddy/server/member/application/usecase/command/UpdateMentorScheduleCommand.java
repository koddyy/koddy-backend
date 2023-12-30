package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.mentor.Schedule;

import java.util.List;

public record UpdateMentorScheduleCommand(
        Long mentorId,
        List<Schedule> schedules
) {
}
