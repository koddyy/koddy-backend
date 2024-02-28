package com.koddy.server.coffeechat.domain.model.response

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.response.LanguageResponse

data class MenteeDetails(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val nationality: String,
    val introduction: String?,
    val languages: LanguageResponse,
    val interestSchool: String,
    val interestMajor: String,
    val status: String,
) {
    companion object {
        @JvmStatic
        fun from(mentee: Mentee): MenteeDetails =
            MenteeDetails(
                mentee.id,
                mentee.name,
                mentee.profileImageUrl,
                mentee.nationality.code,
                mentee.introduction,
                LanguageResponse.of(mentee.languages),
                mentee.interest.school,
                mentee.interest.major,
                mentee.status.name,
            )

        fun of(
            mentee: Mentee,
            languages: List<Language>,
        ): MenteeDetails =
            MenteeDetails(
                mentee.id,
                mentee.name,
                mentee.profileImageUrl,
                mentee.nationality.code,
                mentee.introduction,
                LanguageResponse.of(languages),
                mentee.interest.school,
                mentee.interest.major,
                mentee.status.name,
            )
    }
}
