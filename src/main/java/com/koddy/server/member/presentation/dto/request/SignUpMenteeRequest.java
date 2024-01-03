package com.koddy.server.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

        @NotBlank(message = "국적은 필수입니다.")
        String nationality,

        String introduction,

        @NotEmpty(message = "사용 가능한 언어는 하나 이상 선택해주세요.")
        List<String> languages,

        @NotBlank(message = "관심있는 학교는 필수입니다.")
        String interestSchool,

        @NotBlank(message = "관심있는 전공은 필수입니다.")
        String interestMajor
) {
}
