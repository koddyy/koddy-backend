package com.koddy.server.member.domain.repository.query.response

import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.mentee.Interest

data class AppliedCoffeeChatsByMentee(
    val coffeeChatId: Long,
    val menteeId: Long,
    val name: String,
    val profileImageUrl: String?,
    val nationality: Nationality,
    val interest: Interest,
)
