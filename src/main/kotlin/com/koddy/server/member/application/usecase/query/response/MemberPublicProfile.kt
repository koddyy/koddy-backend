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
        fun from(mentor: Mentor): MentorPublicProfile {
            return MentorPublicProfile(
                id = mentor.id,
                name = mentor.name,
                profileImageUrl = mentor.profileImageUrl,
                introduction = mentor.introduction,
                languages = LanguageResponse.of(mentor.languages),
                school = mentor.universityProfile.school,
                major = mentor.universityProfile.major,
                enteredIn = mentor.universityProfile.enteredIn,
                authenticated = mentor.isAuthenticated,
            )
        }
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
        fun from(mentee: Mentee): MenteePublicProfile {
            return MenteePublicProfile(
                id = mentee.id,
                name = mentee.name,
                profileImageUrl = mentee.profileImageUrl,
                nationality = mentee.nationality.code,
                introduction = mentee.introduction,
                languages = LanguageResponse.of(mentee.languages),
                interestSchool = mentee.interest.school,
                interestMajor = mentee.interest.major,
            )
        }
    }
}
