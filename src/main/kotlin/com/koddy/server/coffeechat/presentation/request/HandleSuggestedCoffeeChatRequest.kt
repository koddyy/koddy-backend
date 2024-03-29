package com.koddy.server.coffeechat.presentation.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.koddy.server.coffeechat.application.usecase.command.PendingSuggestedCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.global.utils.TimeUtils
import jakarta.validation.constraints.NotBlank

data class RejectSuggestedCoffeeChatRequest(
    @field:NotBlank(message = "거절 사유는 필수입니다.")
    val rejectReason: String,
) {
    fun toCommand(
        menteeId: Long,
        coffeeChatId: Long,
    ): RejectSuggestedCoffeeChatCommand {
        return RejectSuggestedCoffeeChatCommand(
            menteeId = menteeId,
            coffeeChatId = coffeeChatId,
            rejectReason = rejectReason,
        )
    }
}

data class PendingSuggestedCoffeeChatRequest(
    @field:NotBlank(message = "멘토에게 궁금한 점은 필수입니다.")
    val question: String,

    @field:NotBlank(message = "멘토링 신청 시작 날짜는 필수입니다.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val start: String,

    @field:NotBlank(message = "멘토링 신청 종료 날짜는 필수입니다.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val end: String,
) {
    fun toCommand(
        menteeId: Long,
        coffeeChatId: Long,
    ): PendingSuggestedCoffeeChatCommand {
        return PendingSuggestedCoffeeChatCommand(
            menteeId = menteeId,
            coffeeChatId = coffeeChatId,
            question = question,
            reservation = Reservation(
                start = TimeUtils.toLocalDateTime(start),
                end = TimeUtils.toLocalDateTime(end),
            ),
        )
    }
}
