package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatByApplyCommand
import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatBySuggestCommand
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatWriter
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader

@UseCase
class CreateCoffeeChatUseCase(
    private val memberReader: MemberReader,
    private val reservationAvailabilityChecker: ReservationAvailabilityChecker,
    private val coffeeChatWriter: CoffeeChatWriter,
    private val eventPublisher: CoffeeChatNotificationEventPublisher,
) {
    @KoddyWritableTransactional
    fun createByApply(command: CreateCoffeeChatByApplyCommand): Long {
        val mentee: Mentee = memberReader.getMentee(command.menteeId)
        val mentor: Mentor = memberReader.getMentor(command.mentorId)
        reservationAvailabilityChecker.check(mentor, command.reservation)

        val coffeeChat: CoffeeChat = coffeeChatWriter.save(
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
        val mentor: Mentor = memberReader.getMentor(command.mentorId)
        val mentee: Mentee = memberReader.getMentee(command.menteeId)

        val coffeeChat: CoffeeChat = coffeeChatWriter.save(
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
