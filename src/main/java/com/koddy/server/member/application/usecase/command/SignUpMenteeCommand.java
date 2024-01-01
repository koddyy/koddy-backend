package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentee.Interest;

import java.util.List;

public record SignUpMenteeCommand(
        Email email,
        String name,
        String profileImageUrl,
        Nationality nationality,
        String introduction,
        List<Language> languages,
        Interest interest
) {
}
