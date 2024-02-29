package com.koddy.server.member.presentation.request

import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel

data class CompleteMentorProfileRequest(
    val introduction: String?,
    val profileImageUrl: String?,
    val period: MentoringPeriodRequestModel?,
    val schedules: List<MentorScheduleRequest> = emptyList(),
) {
    fun toCommand(mentorId: Long): CompleteMentorProfileCommand =
        CompleteMentorProfileCommand(
            mentorId = mentorId,
            introduction = introduction,
            profileImageUrl = profileImageUrl,
            mentoringPeriod = period?.toPeriod(),
            timelines = schedules.map { it.toTimeline() },
        )
}

data class CompleteMenteeProfileRequest(
    val introduction: String?,
    val profileImageUrl: String?,
) {
    fun toCommand(menteeId: Long): CompleteMenteeProfileCommand =
        CompleteMenteeProfileCommand(
            menteeId = menteeId,
            introduction = introduction,
            profileImageUrl = profileImageUrl,
        )
}
