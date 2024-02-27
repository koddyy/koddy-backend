package com.koddy.server.coffeechat.presentation.request

import com.koddy.server.coffeechat.application.usecase.command.ApproveAppliedCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.RejectAppliedCoffeeChatCommand
import com.koddy.server.coffeechat.domain.model.Strategy
import jakarta.validation.constraints.NotBlank

data class RejectAppliedCoffeeChatRequest(
    @field:NotBlank(message = "거절 사유는 필수입니다.")
    val rejectReason: String,
) {
    fun toCommand(
        mentorId: Long,
        coffeeChatId: Long,
    ): RejectAppliedCoffeeChatCommand =
        RejectAppliedCoffeeChatCommand(
            mentorId,
            coffeeChatId,
            rejectReason,
        )
}

data class ApproveAppliedCoffeeChatRequest(
    @field:NotBlank(message = "멘티에게 궁금한 점은 필수입니다.")
    val question: String,

    @field:NotBlank(message = "멘토링 진행 방식은 필수입니다.")
    val chatType: String,

    @field:NotBlank(message = "멘토링 진행 방식에 대한 URL이나 메신저 ID는 필수입니다.")
    val chatValue: String,
) {
    fun toCommand(
        mentorId: Long,
        coffeeChatId: Long,
    ): ApproveAppliedCoffeeChatCommand =
        ApproveAppliedCoffeeChatCommand(
            mentorId,
            coffeeChatId,
            question,
            Strategy.Type.from(chatType),
            chatValue,
        )
}
