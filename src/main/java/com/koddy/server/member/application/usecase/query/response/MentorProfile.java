package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.mentor.ChatTime;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.Schedule;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;

import java.time.LocalTime;
import java.util.List;

public record MentorProfile(
        Long id,
        String email,
        String name,
        String profileImageUrl,
        String nationality,
        String introduction,
        LanguageResponse languages,
        UniversityResponse university,
        List<ScheduleResponse> schedules
) {
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
            String day,
            LocalTime start,
            LocalTime end
    ) {
        public ScheduleResponse(final Schedule schedule) {
            this(
                    schedule.getDayOfWeek().getKor(),
                    schedule.getPeriod().getStartTime(),
                    schedule.getPeriod().getEndTime()
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
                mentor.getChatTimes()
                        .stream()
                        .map(ChatTime::getSchedule)
                        .map(ScheduleResponse::new)
                        .toList()
        );
    }
}
