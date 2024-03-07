package com.koddy.server.notification.presentation.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class GetNotificationsRequest(
    @field:NotNull(message = "페이지 번호는 필수입니다.")
    @field:Min(value = 1, message = "페이지는 1부터 시작입니다.")
    val page: Int,
)
