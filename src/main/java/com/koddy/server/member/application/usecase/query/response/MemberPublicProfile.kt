package com.koddy.server.member.application.usecase.query.response

import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.response.LanguageResponse

data class MentorPublicProfile(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val introduction: String?,
    val languages: LanguageResponse,
    val school: String,
    val major: String,
    val enteredIn: Int,
    val authenticated: Boolean,
) {
    companion object {
        @JvmStatic
        fun from(mentor: Mentor): MentorPublicProfile =
            MentorPublicProfile(
                mentor.id,
                mentor.name,
                mentor.profileImageUrl,
                mentor.introduction,
                LanguageResponse.of(mentor.languages),
                mentor.universityProfile.school,
                mentor.universityProfile.major,
                mentor.universityProfile.enteredIn,
                mentor.isAuthenticated,
            )
    }
}

data class MenteePublicProfile(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val nationality: String,
    val introduction: String?,
    val languages: LanguageResponse,
    val interestSchool: String,
    val interestMajor: String,
) {
    companion object {
        @JvmStatic
        fun from(mentee: Mentee): MenteePublicProfile =
            MenteePublicProfile(
                mentee.id,
                mentee.name,
                mentee.profileImageUrl,
                mentee.nationality.code,
                mentee.introduction,
                LanguageResponse.of(mentee.languages),
                mentee.interest.school,
                mentee.interest.major,
            )
    }
}
