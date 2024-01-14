package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import jakarta.validation.constraints.NotBlank;
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

        @NotNull(message = "사용 가능한 언어를 선택해주세요.")
        LanguageRequest languages,

        @NotBlank(message = "학교는 필수입니다.")
        String school,

        @NotBlank(message = "전공은 필수입니다.")
        String major,

        @NotNull(message = "학번은 필수입니다.")
        Integer enteredIn
) {
    public List<Language> toLanguages() {
        return languages.toLanguages();
    }
}
