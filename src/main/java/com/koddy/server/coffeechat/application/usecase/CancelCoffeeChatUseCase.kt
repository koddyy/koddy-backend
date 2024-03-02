package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase

@UseCase
class CancelCoffeeChatUseCase(
    private val coffeeChatReader: CoffeeChatReader,
    private val eventPublisher: CoffeeChatNotificationEventPublisher,
) {
    @KoddyWritableTransactional
    fun invoke(command: CancelCoffeeChatCommand) {
        val authenticated: Authenticated = command.authenticated

        when (authenticated.isMentor) {
            true -> cancelByMentor(command, authenticated)
            false -> cancelByMentee(command, authenticated)
        }
    }

    private fun cancelByMentor(
        command: CancelCoffeeChatCommand,
        authenticated: Authenticated,
    ) {
        val coffeeChat: CoffeeChat = coffeeChatReader.getByMentor(command.coffeeChatId, authenticated.id)
        coffeeChat.cancel(determineCancelStatus(coffeeChat), authenticated.id, command.cancelReason)

        when (coffeeChat.isMenteeFlow) {
            true -> eventPublisher.publishMentorCanceledFromMenteeFlowEvent(coffeeChat)
            false -> eventPublisher.publishMentorCanceledFromMentorFlowEvent(coffeeChat)
        }
    }

    private fun cancelByMentee(
        command: CancelCoffeeChatCommand,
        authenticated: Authenticated,
    ) {
        val coffeeChat: CoffeeChat = coffeeChatReader.getByMentee(command.coffeeChatId, authenticated.id)
        coffeeChat.cancel(determineCancelStatus(coffeeChat), authenticated.id, command.cancelReason)

        when (coffeeChat.isMenteeFlow) {
            true -> eventPublisher.publishMenteeCanceledFromMenteeFlowEvent(coffeeChat)
            false -> eventPublisher.publishMenteeCanceledFromMentorFlowEvent(coffeeChat)
        }
    }

    private fun determineCancelStatus(coffeeChat: CoffeeChat): CoffeeChatStatus =
        when (coffeeChat.isMenteeFlow) {
            true -> CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW
            false -> CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW
        }
}
