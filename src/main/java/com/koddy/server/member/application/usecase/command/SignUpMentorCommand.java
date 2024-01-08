package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentor.Timeline;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;

import java.util.List;

public record SignUpMentorCommand(
        Email email,
        String name,
        String profileImageUrl,
        String introduction,
        List<Language> languages,
        UniversityProfile universityProfile,
        List<Timeline> timelines
) {
}
