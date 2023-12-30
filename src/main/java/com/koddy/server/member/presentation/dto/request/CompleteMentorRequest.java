package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentor.Period;
import com.koddy.server.member.domain.model.mentor.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CompleteMentorRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "국적은 필수입니다.")
        Nationality nationality,

        String profileUploadUrl,

        String introduction,

        @NotEmpty(message = "사용 가능한 언어는 하나 이상 선택해야 합니다.")
        List<Language> languages,

        @NotBlank(message = "학교 정보는 필수입니다.")
        String school,

        @NotBlank(message = "전공 정보는 필수입니다.")
        String major,

        @NotNull(message = "학년 정보는 필수입니다.")
        Integer grade,

        @NotBlank(message = "커피챗 링크는 필수입니다.")
        String meetingUrl,

        @NotEmpty(message = "멘토링 스케줄은 하루 이상 선택해야 합니다.")
        List<MentorScheduleRequest> schedules
) {
    public List<Schedule> toSchedules() {
        return schedules.stream()
                .map(it -> new Schedule(it.day(), Period.of(it.startTime(), it.endTime())))
                .toList();
    }
}
