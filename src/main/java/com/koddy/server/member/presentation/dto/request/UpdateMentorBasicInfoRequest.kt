package com.koddy.server.member.presentation.dto.request

import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateMentorBasicInfoRequest(
    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String,

    val profileImageUrl: String?,

    val introduction: String?,

    @field:NotNull(message = "사용 가능한 언어를 선택해주세요.")
    val languages: LanguageRequest,

    @field:NotBlank(message = "학교 정보는 필수입니다.")
    val school: String,

    @field:NotBlank(message = "전공 정보는 필수입니다.")
    val major: String,

    @field:NotNull(message = "학번 정보는 필수입니다.")
    val enteredIn: Int,
) {
    fun toCommand(mentorId: Long): UpdateMentorBasicInfoCommand =
        UpdateMentorBasicInfoCommand(
            mentorId,
            name,
            profileImageUrl,
            introduction,
            languages.toLanguages(),
            school,
            major,
            enteredIn,
        )
}
