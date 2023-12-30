package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;

import java.util.List;

public record UpdateMenteeBasicInfoCommand(
        Long menteeId,
        String name,
        Nationality nationality,
        String profileImageUrl,
        String introduction,
        List<Language> languages,
        String interestSchool,
        String interestMajor
) {
}
