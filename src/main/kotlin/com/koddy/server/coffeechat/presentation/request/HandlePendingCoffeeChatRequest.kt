package com.koddy.server.coffeechat.presentation.request

import com.koddy.server.coffeechat.application.usecase.command.FinallyApprovePendingCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.FinallyCancelPendingCoffeeChatCommand
import com.koddy.server.coffeechat.domain.model.Strategy
import jakarta.validation.constraints.NotBlank

data class FinallyCancelPendingCoffeeChatRequest(
    @field:NotBlank(message = "취소 사유는 필수입니다.")
    val cancelReason: String,
) {
    fun toCommand(
        mentorId: Long,
        coffeeChatId: Long,
    ): FinallyCancelPendingCoffeeChatCommand {
        return FinallyCancelPendingCoffeeChatCommand(
            mentorId = mentorId,
            coffeeChatId = coffeeChatId,
            cancelReason = cancelReason,
        )
    }
}

data class FinallyApprovePendingCoffeeChatRequest(
    @field:NotBlank(message = "멘토링 진행 방식은 필수입니다.")
    val chatType: String,

    @field:NotBlank(message = "멘토링 진행 방식에 대한 URL이나 메신저 ID는 필수입니다.")
    val chatValue: String,
) {
    fun toCommand(
        mentorId: Long,
        coffeeChatId: Long,
    ): FinallyApprovePendingCoffeeChatCommand {
        return FinallyApprovePendingCoffeeChatCommand(
            mentorId = mentorId,
            coffeeChatId = coffeeChatId,
            type = Strategy.Type.from(chatType),
            value = chatValue,
        )
    }
}
