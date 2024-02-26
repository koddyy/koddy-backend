package com.koddy.server.member.presentation.request

import com.koddy.server.member.application.usecase.query.GetMentorReservedSchedule
import jakarta.validation.constraints.NotNull

data class GetMentorReservedScheduleRequest(
    @field:NotNull(message = "Year 정보는 필수입니다.")
    val year: Int,

    @field:NotNull(message = "Month 정보는 필수입니다.")
    val month: Int,
) {
    fun toQuery(mentorId: Long): GetMentorReservedSchedule =
        GetMentorReservedSchedule(
            mentorId,
            year,
            month,
        )
}
