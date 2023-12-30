package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Language;

import java.util.List;

public record UpdateMentorBasicInfoCommand(
        Long mentorId,
        String name,
        String profileImageUrl,
        List<Language> languages,
        String school,
        String major,
        int grade,
        String meetingUrl,
        String introduction
) {
}
