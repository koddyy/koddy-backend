package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.MentorNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.member.domain.model.mentor.Mentor
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
class MentorNotificationHandler(
    private val mentorRepository: MentorRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
    private val notificationRepository: NotificationRepository,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMenteeAppliedFromMenteeFlowEvent(event: MentorNotification.MenteeAppliedFromMenteeFlowEvent) {
        notify(
            mentorId = event.mentorId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMenteeCanceledFromMenteeFlowEvent(event: MentorNotification.MenteeCanceledFromMenteeFlowEvent) {
        notify(
            mentorId = event.mentorId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_CANCEL,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMenteeCanceledFromMentorFlowEvent(event: MentorNotification.MenteeCanceledFromMentorFlowEvent) {
        notify(
            mentorId = event.mentorId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_CANCEL,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMenteeRejectedFromMentorFlowEvent(event: MentorNotification.MenteeRejectedFromMentorFlowEvent) {
        notify(
            mentorId = event.mentorId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_REJECT,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMenteePendedFromMentorFlowEvent(event: MentorNotification.MenteePendedFromMentorFlowEvent) {
        notify(
            mentorId = event.mentorId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING,
        )
    }

    private fun notify(
        mentorId: Long,
        coffeeChatId: Long,
        type: NotificationType,
    ) {
        val mentor: Mentor = mentorRepository.getById(mentorId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(coffeeChatId)

        notificationRepository.save(
            Notification.create(
                mentor,
                coffeeChat,
                type,
            ),
        )
    }
}
