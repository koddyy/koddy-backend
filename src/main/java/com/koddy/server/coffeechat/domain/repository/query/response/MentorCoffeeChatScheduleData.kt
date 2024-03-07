package com.koddy.server.coffeechat.domain.repository.query.response

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.member.domain.model.mentee.Interest

data class MentorCoffeeChatScheduleData(
    val id: Long,
    val status: CoffeeChatStatus,
    val menteeId: Long,
    val menteeName: String,
    val menteeProfileImageUrl: String?,
    val menteeInterest: Interest,
)
