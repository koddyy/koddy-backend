package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatByApplyCommand
import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatBySuggestCommand
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository

@UseCase
class CreateCoffeeChatUseCase(
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
    private val reservationAvailabilityChecker: ReservationAvailabilityChecker,
    private val coffeeChatRepository: CoffeeChatRepository,
    private val eventPublisher: CoffeeChatNotificationEventPublisher,
) {
    @KoddyWritableTransactional
    fun createByApply(command: CreateCoffeeChatByApplyCommand): Long {
        val mentee: Mentee = menteeRepository.getById(command.menteeId)
        val mentor: Mentor = mentorRepository.getById(command.mentorId)
        reservationAvailabilityChecker.check(mentor, command.reservation)

        val coffeeChat: CoffeeChat = coffeeChatRepository.save(
            CoffeeChat.apply(
                mentee,
                mentor,
                command.applyReason,
                command.reservation,
            ),
        )
        eventPublisher.publishMenteeAppliedFromMenteeFlowEvent(coffeeChat)
        return coffeeChat.id
    }

    @KoddyWritableTransactional
    fun createBySuggest(command: CreateCoffeeChatBySuggestCommand): Long {
        val mentor: Mentor = mentorRepository.getById(command.mentorId)
        val mentee: Mentee = menteeRepository.getById(command.menteeId)

        val coffeeChat: CoffeeChat = coffeeChatRepository.save(
            CoffeeChat.suggest(
                mentor,
                mentee,
                command.suggestReason,
            ),
        )
        eventPublisher.publishMentorSuggestedFromMentorFlowEvent(coffeeChat)
        return coffeeChat.id
    }
}
