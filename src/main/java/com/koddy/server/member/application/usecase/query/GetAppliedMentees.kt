package com.koddy.server.member.application.usecase.query

data class GetAppliedMentees(
    val mentorId: Long,
    val limit: Int,
)
