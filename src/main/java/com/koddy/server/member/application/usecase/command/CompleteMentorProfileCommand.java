package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.mentor.Timeline;

import java.util.List;

public record CompleteMentorProfileCommand(
        long mentorId,
        String introduction,
        List<Timeline> timelines
) {
}
