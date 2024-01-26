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
        String profileImageUrl,
        Nationality nationality,
        List<Language> languages,
        Interest interest
) {
    public Mentee toDomain() {
        return new Mentee(
                platform,
                name,
                profileImageUrl,
                nationality,
                languages,
                interest
        );
    }
}
