package com.koddy.server.member.application.usecase.command

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.SocialPlatform
import com.koddy.server.member.domain.model.mentee.Interest
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.mentor.UniversityProfile

data class SignUpMentorCommand(
    val platform: SocialPlatform,
    val name: String,
    val languages: List<Language>,
    val universityProfile: UniversityProfile,
) {
    fun toDomain(): Mentor =
        Mentor(
            platform,
            name,
            languages,
            universityProfile,
        )
}

data class SignUpMenteeCommand(
    val platform: SocialPlatform,
    val name: String,
    val nationality: Nationality,
    val languages: List<Language>,
    val interest: Interest,
) {
    fun toDomain(): Mentee =
        Mentee(
            platform,
            name,
            nationality,
            languages,
            interest,
        )
}
