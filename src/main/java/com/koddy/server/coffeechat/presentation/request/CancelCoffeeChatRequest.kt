package com.koddy.server.coffeechat.presentation.request

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand
import jakarta.validation.constraints.NotBlank

data class CancelCoffeeChatRequest(
    @field:NotBlank(message = "취소 사유는 필수입니다.")
    val cancelReason: String,
) {
    fun toCommand(
        authenticated: Authenticated,
        coffeeChatId: Long,
    ): CancelCoffeeChatCommand {
        return CancelCoffeeChatCommand(
            authenticated = authenticated,
            coffeeChatId = coffeeChatId,
            cancelReason = cancelReason,
        )
    }
}
