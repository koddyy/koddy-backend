package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.FinallyApprovePendingCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.FinallyCancelPendingCoffeeChatCommand
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.Strategy
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.global.utils.encrypt.Encryptor

@UseCase
class HandlePendingCoffeeChatUseCase(
    private val coffeeChatReader: CoffeeChatReader,
    private val encryptor: Encryptor,
    private val eventPublisher: CoffeeChatNotificationEventPublisher,
) {
    @KoddyWritableTransactional
    fun finallyCancel(command: FinallyCancelPendingCoffeeChatCommand) {
        val coffeeChat: CoffeeChat = coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId)
        coffeeChat.finallyCancelPendingCoffeeChat(command.cancelReason)
        eventPublisher.publishMentorFinallyCanceledFromMentorFlowEvent(coffeeChat)
    }

    @KoddyWritableTransactional
    fun finallyApprove(command: FinallyApprovePendingCoffeeChatCommand) {
        val coffeeChat: CoffeeChat = coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId)
        coffeeChat.finallyApprovePendingCoffeeChat(Strategy.of(command.type, command.value, encryptor))
        eventPublisher.publishFinallyApprovedFromMentorFlowEvent(coffeeChat)
    }
}
