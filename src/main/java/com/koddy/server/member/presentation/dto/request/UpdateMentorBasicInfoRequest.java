package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateMentorBasicInfoRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
        String profileImageUrl,

        String introduction,

        @NotEmpty(message = "사용 가능한 언어는 하나 이상 선택해야 합니다.")
        List<LanguageRequest> languages,

        @NotBlank(message = "학교 정보는 필수입니다.")
        String school,

        @NotBlank(message = "전공 정보는 필수입니다.")
        String major,

        @NotNull(message = "학번 정보는 필수입니다.")
        Integer enteredIn
) {
    public List<Language> toLanguages() {
        return languages.stream()
                .map(LanguageRequest::toLanguage)
                .toList();
    }
}
