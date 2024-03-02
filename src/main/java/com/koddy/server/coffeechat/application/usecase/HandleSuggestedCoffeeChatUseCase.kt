package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.PendingSuggestedCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MentorRepository

@UseCase
class HandleSuggestedCoffeeChatUseCase(
    private val coffeeChatReader: CoffeeChatReader,
    private val mentorRepository: MentorRepository,
    private val reservationAvailabilityChecker: ReservationAvailabilityChecker,
    private val eventPublisher: CoffeeChatNotificationEventPublisher,
) {
    @KoddyWritableTransactional
    fun reject(command: RejectSuggestedCoffeeChatCommand) {
        val coffeeChat: CoffeeChat = coffeeChatReader.getByMentee(command.coffeeChatId, command.menteeId)
        coffeeChat.rejectFromMentorSuggest(command.rejectReason)
        eventPublisher.publishMenteeRejectedFromMentorFlowEvent(coffeeChat)
    }

    @KoddyWritableTransactional
    fun pending(command: PendingSuggestedCoffeeChatCommand) {
        val coffeeChat: CoffeeChat = coffeeChatReader.getByMentee(command.coffeeChatId, command.menteeId)
        val mentor: Mentor = mentorRepository.getByIdWithSchedules(coffeeChat.mentorId)
        reservationAvailabilityChecker.check(mentor, command.reservation)

        coffeeChat.pendingFromMentorSuggest(command.question, command.reservation)
        eventPublisher.publishMenteePendedFromMentorFlowEvent(coffeeChat)
    }
}
