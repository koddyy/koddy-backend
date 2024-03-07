package com.koddy.server.member.application.usecase.command

import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import com.koddy.server.member.domain.model.mentor.Timeline

data class CompleteMentorProfileCommand(
    val mentorId: Long,
    val introduction: String?,
    val profileImageUrl: String?,
    val mentoringPeriod: MentoringPeriod?,
    val timelines: List<Timeline>,
)

data class CompleteMenteeProfileCommand(
    val menteeId: Long,
    val introduction: String?,
    val profileImageUrl: String?,
)
