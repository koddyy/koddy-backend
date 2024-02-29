package com.koddy.server.member.application.usecase.query.response

import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor

data class MenteeSimpleSearchProfile(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val nationality: String,
    val interestSchool: String,
    val interestMajor: String,
) {
    companion object {
        fun from(mentee: Mentee): MenteeSimpleSearchProfile =
            MenteeSimpleSearchProfile(
                id = mentee.id,
                name = mentee.name,
                profileImageUrl = mentee.profileImageUrl,
                nationality = mentee.nationality.code,
                interestSchool = mentee.interest.school,
                interestMajor = mentee.interest.major,
            )
    }
}

data class MentorSimpleSearchProfile(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val school: String,
    val major: String,
    val enteredIn: Int,
) {
    companion object {
        fun from(mentor: Mentor): MentorSimpleSearchProfile =
            MentorSimpleSearchProfile(
                id = mentor.id,
                name = mentor.name,
                profileImageUrl = mentor.profileImageUrl,
                school = mentor.universityProfile.school,
                major = mentor.universityProfile.major,
                enteredIn = mentor.universityProfile.enteredIn,
            )
    }
}
