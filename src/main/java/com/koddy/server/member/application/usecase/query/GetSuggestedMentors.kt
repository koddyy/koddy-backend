package com.koddy.server.member.application.usecase.query

data class GetSuggestedMentors(
    val menteeId: Long,
    val limit: Int,
)
