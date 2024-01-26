package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.SocialPlatform;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record SignUpMentorRequest(
        @NotBlank(message = "소셜 플랫폼 정보는 필수입니다.")
        String provider,

        @NotBlank(message = "소셜 플랫폼 ID는 필수입니다.")
        String socialId,

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
    public SocialPlatform toSocialPlatform() {
        return new SocialPlatform(
                OAuthProvider.from(provider),
                socialId,
                Email.from(email)
        );
    }

    public List<Language> toLanguages() {
        return languages.toLanguages();
    }

    public UniversityProfile toUniversityProfile() {
        return new UniversityProfile(school, major, enteredIn);
    }
}
