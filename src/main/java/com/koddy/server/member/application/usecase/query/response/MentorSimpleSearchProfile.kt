package com.koddy.server.member.application.usecase.query.response

import com.koddy.server.member.domain.model.mentor.Mentor

data class MentorSimpleSearchProfile(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
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
