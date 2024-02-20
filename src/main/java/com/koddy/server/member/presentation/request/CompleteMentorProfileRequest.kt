package com.koddy.server.member.presentation.request

import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand

data class CompleteMentorProfileRequest(
    val introduction: String?,
    val profileImageUrl: String?,
    val period: MentoringPeriodRequest?,
    val schedules: List<MentorScheduleRequest> = emptyList(),
) {
    fun toCommand(mentorId: Long): CompleteMentorProfileCommand =
        CompleteMentorProfileCommand(
            mentorId,
            introduction,
            profileImageUrl,
            period?.toPeriod(),
            schedules.map { it.toTimeline() },
        )
}
