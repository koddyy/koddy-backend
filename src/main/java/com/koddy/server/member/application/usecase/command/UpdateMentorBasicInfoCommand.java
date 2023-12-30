package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;

import java.util.List;

public record UpdateMentorBasicInfoCommand(
        Long mentorId,
        String name,
        String profileImageUrl,
        List<Language> languages,
        UniversityProfile universityProfile,
        String meetingUrl,
        String introduction
) {
}
