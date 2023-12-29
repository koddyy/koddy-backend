package com.koddy.server.member.application.usecase.command;

import com.koddy.server.file.domain.model.RawFileData;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentee.Interest;

import java.util.List;

public record CompleteMenteeCommand(
        Long menteeId,
        String name,
        Nationality nationality,
        RawFileData profile,
        List<Language> languages,
        Interest interest
) {
}
