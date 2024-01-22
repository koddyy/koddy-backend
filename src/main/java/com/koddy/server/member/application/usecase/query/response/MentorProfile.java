package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.Mentor;

import java.util.List;

public record MentorProfile(
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
) implements MemberProfile {
    public static MentorProfile of(final Mentor mentor) {
        return new MentorProfile(
                mentor.getId(),
                mentor.getEmail().getValue(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getNationality().getKor(),
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
