package com.koddy.server.coffeechat.domain.model.response

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.response.LanguageResponse

class MentorDetails(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val introduction: String?,
    val languages: LanguageResponse,
    val school: String,
    val major: String,
    val enteredIn: Int,
    val status: String,
) {
    companion object {
        @JvmStatic
        fun from(mentor: Mentor): MentorDetails =
            MentorDetails(
                mentor.id,
                mentor.name,
                mentor.profileImageUrl,
                mentor.introduction,
                LanguageResponse.of(mentor.languages),
                mentor.universityProfile.school,
                mentor.universityProfile.major,
                mentor.universityProfile.enteredIn,
                mentor.status.name,
            )

        fun of(
            mentor: Mentor,
            languages: List<Language>,
        ): MentorDetails =
            MentorDetails(
                mentor.id,
                mentor.name,
                mentor.profileImageUrl,
                mentor.introduction,
                LanguageResponse.of(languages),
                mentor.universityProfile.school,
                mentor.universityProfile.major,
                mentor.universityProfile.enteredIn,
                mentor.status.name,
            )
    }
}
