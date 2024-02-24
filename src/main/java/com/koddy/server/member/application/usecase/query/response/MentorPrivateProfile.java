package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.response.LanguageResponse;
import com.koddy.server.member.domain.model.response.MentoringPeriodResponse;
import com.koddy.server.member.domain.model.response.ScheduleResponse;

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
    public static MentorPrivateProfile from(final Mentor mentor) {
        return new MentorPrivateProfile(
                mentor.getId(),
                mentor.getPlatform().getEmail().getValue(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getNationality().code,
                mentor.getIntroduction(),
                LanguageResponse.of(mentor.getLanguages()),
                mentor.getUniversityProfile().getSchool(),
                mentor.getUniversityProfile().getMajor(),
                mentor.getUniversityProfile().getEnteredIn(),
                mentor.isAuthenticated(),
                MentoringPeriodResponse.from(mentor.getMentoringPeriod()),
                mentor.getSchedules()
                        .stream()
                        .map(it -> ScheduleResponse.from(it.getTimeline()))
                        .toList(),
                "mentor",
                mentor.isProfileComplete()
        );
    }
}
