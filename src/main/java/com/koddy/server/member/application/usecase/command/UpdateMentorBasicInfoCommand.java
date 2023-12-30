package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;

import java.util.List;

public record UpdateMentorBasicInfoCommand(
        Long mentorId,
        String name,
        Nationality nationality,
        String profileImageUrl,
        String introduction,
        List<Language> languages,
        String school,
        String major,
        int grade,
        String meetingUrl
) {
}
