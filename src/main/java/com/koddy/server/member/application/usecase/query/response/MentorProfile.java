package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import com.koddy.server.member.domain.model.mentor.Timeline;
import com.koddy.server.member.domain.model.mentor.UniversityAuthentication;

import java.time.LocalDate;
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
    public record MentoringPeriodResponse(
            LocalDate startDate,
            LocalDate endDate
    ) {
        public static MentoringPeriodResponse of(final MentoringPeriod mentoringPeriod) {
            if (mentoringPeriod == null) {
                return null;
            }
            return new MentoringPeriodResponse(mentoringPeriod.getStartDate(), mentoringPeriod.getEndDate());
        }
    }

    public record ScheduleResponse(
            String dayOfWeek,
            Start start,
            End end
    ) {
        public record Start(
                int hour,
                int minute
        ) {
        }

        public record End(
                int hour,
                int minute
        ) {
        }

        public static ScheduleResponse of(final Timeline timeline) {
            return new ScheduleResponse(
                    timeline.getDayOfWeek().getKor(),
                    new Start(timeline.getStartTime().getHour(), timeline.getStartTime().getMinute()),
                    new End(timeline.getEndTime().getHour(), timeline.getEndTime().getMinute())
            );
        }
    }

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
                isAuthenticated(mentor.getUniversityAuthentication()),
                MentoringPeriodResponse.of(mentor.getMentoringPeriod()),
                mentor.getSchedules()
                        .stream()
                        .map(it -> ScheduleResponse.of(it.getTimeline()))
                        .toList(),
                "mentor",
                mentor.isProfileComplete()
        );
    }

    private static boolean isAuthenticated(final UniversityAuthentication universityAuthentication) {
        if (universityAuthentication == null) {
            return false;
        }
        return universityAuthentication.isAuthenticated();
    }
}
