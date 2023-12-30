package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentor.Schedule;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;

import java.util.List;

public record CompleteMentorCommand(
        Long mentorId,
        String name,
        Nationality nationality,
        String profileUploadUrl,
        String introduction,
        List<Language> languages,
        UniversityProfile universityProfile,
        String meetingUrl,
        List<Schedule> schedules
) {
}
