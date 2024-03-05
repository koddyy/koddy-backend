package com.koddy.server.member.domain.repository.query.response

import com.koddy.server.member.domain.model.mentor.UniversityProfile

data class SuggestedCoffeeChatsByMentor(
    val coffeeChatId: Long,
    val mentorId: Long,
    val name: String,
    val profileImageUrl: String,
    val universityProfile: UniversityProfile,
)
