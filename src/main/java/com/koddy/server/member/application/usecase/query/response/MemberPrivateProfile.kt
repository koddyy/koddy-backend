package com.koddy.server.member.application.usecase.query.response

import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.response.LanguageResponse
import com.koddy.server.member.domain.model.response.MentoringPeriodResponse
import com.koddy.server.member.domain.model.response.ScheduleResponse

sealed interface MemberPrivateProfile

data class MentorPrivateProfile(
    val id: Long,
    val email: String,
    val name: String,
    val profileImageUrl: String?,
    val nationality: String,
    val introduction: String?,
    val languages: LanguageResponse,
    val school: String,
    val major: String,
    val enteredIn: Int,
    val authenticated: Boolean,
    val period: MentoringPeriodResponse?,
    val schedules: List<ScheduleResponse> = emptyList(),
    val role: String,
    val profileComplete: Boolean,
) : MemberPrivateProfile {
    companion object {
        @JvmStatic
        fun from(mentor: Mentor): MentorPrivateProfile =
            MentorPrivateProfile(
                mentor.id,
                mentor.platform.email.value,
                mentor.name,
                mentor.profileImageUrl,
                mentor.nationality.code,
                mentor.introduction,
                LanguageResponse.of(mentor.languages),
                mentor.universityProfile.school,
                mentor.universityProfile.major,
                mentor.universityProfile.enteredIn,
                mentor.isAuthenticated,
                MentoringPeriodResponse.from(mentor.mentoringPeriod),
                mentor.schedules.map { ScheduleResponse.from(it.timeline) },
                "mentor",
                mentor.isProfileComplete,
            )
    }
}

data class MenteePrivateProfile(
    val id: Long,
    val email: String,
    val name: String,
    val profileImageUrl: String?,
    val nationality: String,
    val introduction: String?,
    val languages: LanguageResponse,
    val interestSchool: String,
    val interestMajor: String,
    val role: String,
    val profileComplete: Boolean,
) : MemberPrivateProfile {
    companion object {
        @JvmStatic
        fun from(mentee: Mentee): MenteePrivateProfile =
            MenteePrivateProfile(
                mentee.id,
                mentee.platform.email.value,
                mentee.name,
                mentee.profileImageUrl,
                mentee.nationality.code,
                mentee.introduction,
                LanguageResponse.of(mentee.languages),
                mentee.interest.school,
                mentee.interest.major,
                "mentee",
                mentee.isProfileComplete,
            )
    }
}
