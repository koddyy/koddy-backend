package com.koddy.server.member.presentation.request

import jakarta.validation.constraints.NotNull

data class GetMentorReservedScheduleRequest(
    @NotNull(message = "Year 정보는 필수입니다.")
    val year: Int,

    @NotNull(message = "Month 정보는 필수입니다.")
    val month: Int,
)
