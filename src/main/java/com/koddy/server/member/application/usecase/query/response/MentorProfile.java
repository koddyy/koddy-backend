package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.Timeline;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;

import java.time.LocalDate;
import java.util.List;

import static com.koddy.server.member.domain.model.ProfileComplete.YES;

public record MentorProfile(
        long id,
        String email,
        String name,
        String profileImageUrl,
        String nationality,
        String introduction,
        LanguageResponse languages,
        UniversityResponse university,
        List<ScheduleResponse> schedules,
        String role,
        boolean profileComplete
) implements MemberProfile {
    public record UniversityResponse(
            String school,
            String major,
            int enteredIn
    ) {
        public UniversityResponse(final UniversityProfile profile) {
            this(profile.getSchool(), profile.getMajor(), profile.getEnteredIn());
        }
    }

    public record ScheduleResponse(
            LocalDate startDate,
            LocalDate endDate,
            String dayOfWeek,
            Start startTime,
            End endTime
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

        public ScheduleResponse(final Timeline timeline) {
            this(
                    timeline.getStartDate(),
                    timeline.getEndDate(),
                    timeline.getDayOfWeek().getKor(),
                    new Start(timeline.getPeriod().getStartTime().getHour(), timeline.getPeriod().getStartTime().getMinute()),
                    new End(timeline.getPeriod().getEndTime().getHour(), timeline.getPeriod().getEndTime().getMinute())
            );
        }
    }

    public MentorProfile(final Mentor mentor) {
        this(
                mentor.getId(),
                mentor.getEmail().getValue(),
                mentor.getName(),
                mentor.getProfileImageUrl(),
                mentor.getNationality().getKor(),
                mentor.getIntroduction(),
                new LanguageResponse(mentor.getLanguages()),
                new UniversityResponse(mentor.getUniversityProfile()),
                mentor.getSchedules()
                        .stream()
                        .map(it -> new ScheduleResponse(it.getTimeline()))
                        .toList(),
                "mentor",
                mentor.getProfileComplete() == YES
        );
    }
}
