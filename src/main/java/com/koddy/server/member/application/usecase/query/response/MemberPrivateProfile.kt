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
        fun from(mentor: Mentor): MentorPrivateProfile {
            return MentorPrivateProfile(
                id = mentor.id,
                email = mentor.platform.email!!.value,
                name = mentor.name,
                profileImageUrl = mentor.profileImageUrl,
                nationality = mentor.nationality.code,
                introduction = mentor.introduction,
                languages = LanguageResponse.of(mentor.languages),
                school = mentor.universityProfile.school,
                major = mentor.universityProfile.major,
                enteredIn = mentor.universityProfile.enteredIn,
                authenticated = mentor.isAuthenticated,
                period = MentoringPeriodResponse.from(mentor.mentoringPeriod),
                schedules = mentor.schedules.map { ScheduleResponse.from(it.timeline) },
                role = "mentor",
                profileComplete = mentor.isProfileComplete,
            )
        }
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
        fun from(mentee: Mentee): MenteePrivateProfile {
            return MenteePrivateProfile(
                id = mentee.id,
                email = mentee.platform.email!!.value,
                name = mentee.name,
                profileImageUrl = mentee.profileImageUrl,
                nationality = mentee.nationality.code,
                introduction = mentee.introduction,
                languages = LanguageResponse.of(mentee.languages),
                interestSchool = mentee.interest.school,
                interestMajor = mentee.interest.major,
                role = "mentee",
                profileComplete = mentee.isProfileComplete,
            )
        }
    }
}
