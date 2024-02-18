package com.koddy.server.member.presentation.request

import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand

data class UpdateMentorScheduleRequest(
    val period: MentoringPeriodRequest?,
    val schedules: List<MentorScheduleRequest?>?,
) {
    fun toCommand(mentorId: Long): UpdateMentorScheduleCommand =
        UpdateMentorScheduleCommand(
            mentorId,
            period?.toPeriod(),
            schedules?.filterNotNull()
                ?.map { it.toTimeline() },
        )
}
