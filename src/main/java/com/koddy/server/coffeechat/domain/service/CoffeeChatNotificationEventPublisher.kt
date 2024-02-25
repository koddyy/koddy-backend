package com.koddy.server.coffeechat.domain.service

import com.koddy.server.coffeechat.domain.event.BothNotification
import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.event.MentorNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class CoffeeChatNotificationEventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) {
    /**
     * Notify To Mentor
     */
    fun publishMenteeAppliedFromMenteeFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MentorNotification.MenteeAppliedFromMenteeFlowEvent(
                mentorId = coffeeChat.mentorId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentor
     */
    fun publishMenteeCanceledFromMenteeFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MentorNotification.MenteeCanceledFromMenteeFlowEvent(
                mentorId = coffeeChat.mentorId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentor
     */
    fun publishMenteeCanceledFromMentorFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MentorNotification.MenteeCanceledFromMentorFlowEvent(
                mentorId = coffeeChat.mentorId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentor
     */
    fun publishMenteeRejectedFromMentorFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MentorNotification.MenteeRejectedFromMentorFlowEvent(
                mentorId = coffeeChat.mentorId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentor
     */
    fun publishMenteePendedFromMentorFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MentorNotification.MenteePendedFromMentorFlowEvent(
                mentorId = coffeeChat.mentorId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentee
     */
    fun publishMentorCanceledFromMenteeFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MenteeNotification.MentorCanceledFromMenteeFlowEvent(
                menteeId = coffeeChat.menteeId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentee
     */
    fun publishMentorRejectedFromMenteeFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MenteeNotification.MentorRejectedFromMenteeFlowEvent(
                menteeId = coffeeChat.menteeId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentee
     */
    fun publishMentorSuggestedFromMentorFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MenteeNotification.MentorSuggestedFromMentorFlowEvent(
                menteeId = coffeeChat.menteeId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentee
     */
    fun publishMentorCanceledFromMentorFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MenteeNotification.MentorCanceledFromMentorFlowEvent(
                menteeId = coffeeChat.menteeId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentee
     */
    fun publishMentorFinallyCanceledFromMentorFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            MenteeNotification.MentorFinallyCanceledFromMentorFlowEvent(
                menteeId = coffeeChat.menteeId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentee & Mentor
     */
    fun publishApprovedFromMenteeFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            BothNotification.ApprovedFromMenteeFlowEvent(
                menteeId = coffeeChat.menteeId,
                mentorId = coffeeChat.mentorId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }

    /**
     * Notify To Mentor & Mentee
     */
    fun publishFinallyApprovedFromMentorFlowEvent(coffeeChat: CoffeeChat) {
        eventPublisher.publishEvent(
            BothNotification.FinallyApprovedFromMentorFlowEvent(
                menteeId = coffeeChat.menteeId,
                mentorId = coffeeChat.mentorId,
                coffeeChatId = coffeeChat.id,
            ),
        )
    }
}
