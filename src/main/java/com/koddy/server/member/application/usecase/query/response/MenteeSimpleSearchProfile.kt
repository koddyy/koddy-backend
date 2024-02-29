package com.koddy.server.member.application.usecase.query.response

import com.koddy.server.member.domain.model.mentee.Mentee

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
