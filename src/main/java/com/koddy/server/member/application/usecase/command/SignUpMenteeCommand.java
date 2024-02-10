package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.SocialPlatform;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentee.Mentee;

import java.util.List;

public record SignUpMenteeCommand(
        SocialPlatform platform,
        String name,
        Nationality nationality,
        String profileImageUrl,
        List<Language> languages,
        Interest interest
) {
    public Mentee toDomain() {
        return new Mentee(
                platform,
                name,
                nationality,
                profileImageUrl,
                languages,
                interest
        );
    }
}
