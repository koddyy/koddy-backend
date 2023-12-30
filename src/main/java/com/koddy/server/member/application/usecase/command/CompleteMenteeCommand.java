package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentee.Interest;

import java.util.List;

public record CompleteMenteeCommand(
        Long menteeId,
        String name,
        Nationality nationality,
        String profileUploadUrl,
        List<Language> languages,
        Interest interest
) {
}
