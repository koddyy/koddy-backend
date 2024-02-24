package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.model.NotificationType
import com.koddy.server.notification.domain.repository.NotificationRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MenteeNotificationHandler(
    private val menteeRepository: MenteeRepository,
    private val mentorRepository: MentorRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
    private val notificationRepository: NotificationRepository,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorCanceledFromMenteeFlowEvent(event: MenteeNotification.MentorCanceledFromMenteeFlowEvent) {
        val mentee: Mentee = menteeRepository.getById(event.menteeId)
        val mentor: Mentor = mentorRepository.getById(event.mentorId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(event.coffeeChatId)

        val type: NotificationType = NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_CANCEL
        notificationRepository.save(
            Notification.create(
                mentee,
                coffeeChat,
                type,
                type.createMenteeNotification(
                    mentorName = mentor.name,
                    reason = coffeeChat.reason,
                    reservation = coffeeChat.reservation,
                ),
            ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorRejectedFromMenteeFlowEvent(event: MenteeNotification.MentorRejectedFromMenteeFlowEvent) {
        val mentee: Mentee = menteeRepository.getById(event.menteeId)
        val mentor: Mentor = mentorRepository.getById(event.mentorId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(event.coffeeChatId)

        val type: NotificationType = NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_REJECT
        notificationRepository.save(
            Notification.create(
                mentee,
                coffeeChat,
                type,
                type.createMenteeNotification(
                    mentorName = mentor.name,
                    reason = coffeeChat.reason,
                    reservation = coffeeChat.reservation,
                ),
            ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorSuggestedFromMentorFlowEvent(event: MenteeNotification.MentorSuggestedFromMentorFlowEvent) {
        val mentee: Mentee = menteeRepository.getById(event.menteeId)
        val mentor: Mentor = mentorRepository.getById(event.mentorId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(event.coffeeChatId)

        val type: NotificationType = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST
        notificationRepository.save(
            Notification.create(
                mentee,
                coffeeChat,
                type,
                type.createMenteeNotification(
                    mentorName = mentor.name,
                    reason = coffeeChat.reason,
                    reservation = coffeeChat.reservation,
                ),
            ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorCanceledFromMentorFlowEvent(event: MenteeNotification.MentorCanceledFromMentorFlowEvent) {
        val mentee: Mentee = menteeRepository.getById(event.menteeId)
        val mentor: Mentor = mentorRepository.getById(event.mentorId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(event.coffeeChatId)

        val type: NotificationType = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_CANCEL
        notificationRepository.save(
            Notification.create(
                mentee,
                coffeeChat,
                type,
                type.createMenteeNotification(
                    mentorName = mentor.name,
                    reason = coffeeChat.reason,
                    reservation = coffeeChat.reservation,
                ),
            ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorFinallyCanceledFromMentorFlowEvent(event: MenteeNotification.MentorFinallyCanceledFromMentorFlowEvent) {
        val mentee: Mentee = menteeRepository.getById(event.menteeId)
        val mentor: Mentor = mentorRepository.getById(event.mentorId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(event.coffeeChatId)

        val type: NotificationType = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL
        notificationRepository.save(
            Notification.create(
                mentee,
                coffeeChat,
                type,
                type.createMenteeNotification(
                    mentorName = mentor.name,
                    reason = coffeeChat.reason,
                    reservation = coffeeChat.reservation,
                ),
            ),
        )
    }
}
