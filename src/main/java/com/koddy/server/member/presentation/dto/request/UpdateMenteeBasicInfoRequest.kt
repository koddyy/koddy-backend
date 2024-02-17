package com.koddy.server.member.presentation.dto.request

import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand
import com.koddy.server.member.domain.model.Nationality
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateMenteeBasicInfoRequest(
    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String,

    @field:NotBlank(message = "국적은 필수입니다.")
    val nationality: String,

    val profileImageUrl: String?,

    val introduction: String?,

    @field:NotNull(message = "사용 가능한 언어를 선택해주세요.")
    val languages: LanguageRequest,

    @field:NotBlank(message = "관심있는 학교 정보는 필수입니다.")
    val interestSchool: String,

    @field:NotBlank(message = "관심있는 전공 정보는 필수입니다.")
    val interestMajor: String,
) {
    fun toCommand(menteeId: Long): UpdateMenteeBasicInfoCommand =
        UpdateMenteeBasicInfoCommand(
            menteeId,
            name,
            Nationality.from(nationality),
            profileImageUrl,
            introduction,
            languages.toLanguages(),
            interestSchool,
            interestMajor,
        )
}
