package com.koddy.server.member.application.usecase.query

data class GetMentorReservedSchedule(
    val mentorId: Long,
    val year: Int,
    val month: Int,
)
