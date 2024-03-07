package com.koddy.server.member.application.usecase.command

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import com.koddy.server.member.domain.model.mentor.Timeline

data class UpdateMenteeBasicInfoCommand(
    val menteeId: Long,
    val name: String,
    val nationality: Nationality,
    val profileImageUrl: String?,
    val introduction: String?,
    val languages: List<Language>,
    val interestSchool: String,
    val interestMajor: String,
)

data class UpdateMentorBasicInfoCommand(
    val mentorId: Long,
    val name: String,
    val profileImageUrl: String?,
    val introduction: String?,
    val languages: List<Language>,
    val school: String,
    val major: String,
    val enteredIn: Int,
)

data class UpdateMentorScheduleCommand(
    val mentorId: Long,
    val mentoringPeriod: MentoringPeriod?,
    val timelines: List<Timeline>,
)
