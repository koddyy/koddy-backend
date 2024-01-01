package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentor.Period;
import com.koddy.server.member.domain.model.mentor.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record SignUpMentorRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
        String profileImageUrl,

        @NotBlank(message = "자기소개는 필수입니다.")
        String introduction,

        @NotEmpty(message = "사용 가능한 언어는 하나 이상 선택해주세요.")
        List<Language> languages,

        @NotBlank(message = "학교는 필수입니다.")
        String school,

        @NotBlank(message = "전공은 필수입니다.")
        String major,

        @NotNull(message = "학년은 필수입니다.")
        Integer grade,

        String meetingUrl,

        List<MentorScheduleRequest> schedules
) {
    public List<Schedule> toSchedules() {
        if (schedules.isEmpty()) {
            return List.of();
        }
        return schedules.stream()
                .map(it -> new Schedule(it.day(), Period.of(it.startTime(), it.endTime())))
                .toList();
    }
}
