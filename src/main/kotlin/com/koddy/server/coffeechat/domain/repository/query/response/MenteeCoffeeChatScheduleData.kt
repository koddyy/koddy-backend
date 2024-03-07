package com.koddy.server.coffeechat.domain.repository.query.response

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.member.domain.model.mentor.UniversityProfile

data class MenteeCoffeeChatScheduleData(
    val id: Long,
    val status: CoffeeChatStatus,
    val mentorId: Long,
    val mentorName: String,
    val mentorProfileImageUrl: String?,
    val mentorUniversityProfile: UniversityProfile,
)
