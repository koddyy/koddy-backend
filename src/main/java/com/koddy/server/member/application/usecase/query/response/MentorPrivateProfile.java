package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.Mentor;

import java.util.List;

public record MentorPrivateProfile(
        long id,
        String email,
        String name,
        String profileImageUrl,
        String nationality,
        String introduction,
        LanguageResponse languages,
        String school,
        String major,
        int enteredIn,
        boolean authenticated,
        MentoringPeriodResponse period,
        List<ScheduleResponse> schedules,
        String role,
        boolean profileComplete
) implements MemberPrivateProfile {
    public static MentorPrivateProfile of(final Mentor mentor) {
        return new MentorPrivateProfile(
                mentor.getId(),
                mentor.getPlatform().getEmail().getValue(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getNationality().getValue(),
                mentor.getIntroduction(),
                LanguageResponse.of(mentor.getLanguages()),
                mentor.getUniversityProfile().getSchool(),
                mentor.getUniversityProfile().getMajor(),
                mentor.getUniversityProfile().getEnteredIn(),
                mentor.isAuthenticated(),
                MentoringPeriodResponse.of(mentor.getMentoringPeriod()),
                mentor.getSchedules()
                        .stream()
                        .map(it -> ScheduleResponse.of(it.getTimeline()))
                        .toList(),
                "mentor",
                mentor.isProfileComplete()
        );
    }
}
