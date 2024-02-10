package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.SocialPlatform;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;

import java.util.List;

public record SignUpMentorCommand(
        SocialPlatform platform,
        String name,
        List<Language> languages,
        UniversityProfile universityProfile
) {
    public Mentor toDomain() {
        return new Mentor(
                platform,
                name,
                languages,
                universityProfile
        );
    }
}
