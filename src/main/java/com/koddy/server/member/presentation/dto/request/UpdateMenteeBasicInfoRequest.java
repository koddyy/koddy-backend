package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateMenteeBasicInfoRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "국적은 필수입니다.")
        String nationality,

        @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
        String profileImageUrl,

        String introduction,

        @NotNull(message = "사용 가능한 언어를 선택해주세요.")
        LanguageRequest languages,

        @NotBlank(message = "관심있는 학교 정보는 필수입니다.")
        String interestSchool,

        @NotBlank(message = "관심있는 전공 정보는 필수입니다.")
        String interestMajor
) {
    public Nationality toNationality() {
        return Nationality.from(nationality);
    }

    public List<Language> toLanguages() {
        return languages.toLanguages();
    }
}
