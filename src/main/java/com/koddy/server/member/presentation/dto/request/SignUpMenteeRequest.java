package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record SignUpMenteeRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
        String profileImageUrl,

        @NotNull(message = "국적은 필수입니다.")
        Nationality nationality,

        String introduction,

        @NotEmpty(message = "사용 가능한 언어는 하나 이상 선택해주세요.")
        List<Language> languages,

        @NotBlank(message = "관심있는 학교는 필수입니다.")
        String interestSchool,

        @NotBlank(message = "관심있는 전공은 필수입니다.")
        String interestMajor
) {
}
