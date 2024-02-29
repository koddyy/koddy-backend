package com.koddy.server.member.application.usecase.query.response

import com.koddy.server.member.domain.repository.query.response.SuggestedCoffeeChatsByMentor

data class SuggestedCoffeeChatsByMentorResponse(
    val coffeeChatId: Long,
    val mentorId: Long,
    val name: String,
    val profileImageUrl: String?,
    val school: String,
    val major: String,
    val enteredIn: Int,
) {
    companion object {
        fun from(result: SuggestedCoffeeChatsByMentor): SuggestedCoffeeChatsByMentorResponse =
            SuggestedCoffeeChatsByMentorResponse(
                coffeeChatId = result.coffeeChatId,
                mentorId = result.mentorId,
                name = result.name,
                profileImageUrl = result.profileImageUrl,
                school = result.universityProfile.school,
                major = result.universityProfile.major,
                enteredIn = result.universityProfile.enteredIn,
            )
    }
}
