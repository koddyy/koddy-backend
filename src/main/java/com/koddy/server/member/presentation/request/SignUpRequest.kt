package com.koddy.server.member.presentation.request

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand
import com.koddy.server.member.domain.model.Email
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.SocialPlatform
import com.koddy.server.member.domain.model.mentee.Interest
import com.koddy.server.member.domain.model.mentor.UniversityProfile
import com.koddy.server.member.presentation.request.model.LanguageRequestModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SignUpMentorRequest(
    @field:NotBlank(message = "소셜 플랫폼 정보는 필수입니다.")
    val provider: String,

    @field:NotBlank(message = "소셜 플랫폼 ID는 필수입니다.")
    val socialId: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    val email: String,

    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String,

    @field:NotNull(message = "사용 가능한 언어를 선택해주세요.")
    val languages: LanguageRequestModel,

    @field:NotBlank(message = "학교는 필수입니다.")
    val school: String,

    @field:NotBlank(message = "전공은 필수입니다.")
    val major: String,

    @field:NotNull(message = "학번은 필수입니다.")
    val enteredIn: Int,
) {
    fun toCommand(): SignUpMentorCommand =
        SignUpMentorCommand(
            SocialPlatform(
                OAuthProvider.from(provider),
                socialId,
                Email.from(email),
            ),
            name,
            languages.toLanguages(),
            UniversityProfile(school, major, enteredIn),
        )
}

data class SignUpMenteeRequest(
    @field:NotBlank(message = "소셜 플랫폼 정보는 필수입니다.")
    val provider: String,

    @field:NotBlank(message = "소셜 플랫폼 ID는 필수입니다.")
    val socialId: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    val email: String,

    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String,

    @field:NotBlank(message = "국적은 필수입니다.")
    val nationality: String,

    @field:NotNull(message = "사용 가능한 언어를 선택해주세요.")
    val languages: LanguageRequestModel,

    @field:NotBlank(message = "관심있는 학교는 필수입니다.")
    val interestSchool: String,

    @field:NotBlank(message = "관심있는 전공은 필수입니다.")
    val interestMajor: String,
) {
    fun toCommand(): SignUpMenteeCommand =
        SignUpMenteeCommand(
            SocialPlatform(
                OAuthProvider.from(provider),
                socialId,
                Email.from(email),
            ),
            name,
            Nationality.from(nationality),
            languages.toLanguages(),
            Interest(interestSchool, interestMajor),
        )
}
