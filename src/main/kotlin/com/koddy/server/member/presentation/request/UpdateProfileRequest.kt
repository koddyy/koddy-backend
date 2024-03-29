package com.koddy.server.member.presentation.request

import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.presentation.request.model.LanguageRequestModel
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel
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
    val languages: LanguageRequestModel,

    @field:NotBlank(message = "관심있는 학교 정보는 필수입니다.")
    val interestSchool: String,

    @field:NotBlank(message = "관심있는 전공 정보는 필수입니다.")
    val interestMajor: String,
) {
    fun toCommand(menteeId: Long): UpdateMenteeBasicInfoCommand {
        return UpdateMenteeBasicInfoCommand(
            menteeId = menteeId,
            name = name,
            nationality = Nationality.from(nationality),
            profileImageUrl = profileImageUrl,
            introduction = introduction,
            languages = languages.toLanguages(),
            interestSchool = interestSchool,
            interestMajor = interestMajor,
        )
    }
}

data class UpdateMentorBasicInfoRequest(
    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String,

    val profileImageUrl: String?,

    val introduction: String?,

    @field:NotNull(message = "사용 가능한 언어를 선택해주세요.")
    val languages: LanguageRequestModel,

    @field:NotBlank(message = "학교 정보는 필수입니다.")
    val school: String,

    @field:NotBlank(message = "전공 정보는 필수입니다.")
    val major: String,

    @field:NotNull(message = "학번 정보는 필수입니다.")
    val enteredIn: Int,
) {
    fun toCommand(mentorId: Long): UpdateMentorBasicInfoCommand {
        return UpdateMentorBasicInfoCommand(
            mentorId = mentorId,
            name = name,
            profileImageUrl = profileImageUrl,
            introduction = introduction,
            languages = languages.toLanguages(),
            school = school,
            major = major,
            enteredIn = enteredIn,
        )
    }
}

data class UpdateMentorScheduleRequest(
    val period: MentoringPeriodRequestModel?,
    val schedules: List<MentorScheduleRequest> = emptyList(),
) {
    fun toCommand(mentorId: Long): UpdateMentorScheduleCommand {
        return UpdateMentorScheduleCommand(
            mentorId = mentorId,
            mentoringPeriod = period?.toPeriod(),
            timelines = schedules.map { it.toTimeline() },
        )
    }
}
