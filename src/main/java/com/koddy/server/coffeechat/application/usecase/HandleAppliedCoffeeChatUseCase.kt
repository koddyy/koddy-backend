package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.ApproveAppliedCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.RejectAppliedCoffeeChatCommand
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.Strategy
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.global.utils.encrypt.Encryptor

@UseCase
class HandleAppliedCoffeeChatUseCase(
    private val coffeeChatRepository: CoffeeChatRepository,
    private val encryptor: Encryptor,
    private val eventPublisher: CoffeeChatNotificationEventPublisher,
) {
    @KoddyWritableTransactional
    fun reject(command: RejectAppliedCoffeeChatCommand) {
        val coffeeChat: CoffeeChat = coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId, command.mentorId)
        coffeeChat.rejectFromMenteeApply(command.rejectReason)
        eventPublisher.publishMentorRejectedFromMenteeFlowEvent(coffeeChat)
    }

    @KoddyWritableTransactional
    fun approve(command: ApproveAppliedCoffeeChatCommand) {
        val coffeeChat: CoffeeChat = coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId, command.mentorId)
        coffeeChat.approveFromMenteeApply(
            command.question,
            Strategy.of(command.type, command.value, encryptor),
        )
        eventPublisher.publishApprovedFromMenteeFlowEvent(coffeeChat)
    }
}
