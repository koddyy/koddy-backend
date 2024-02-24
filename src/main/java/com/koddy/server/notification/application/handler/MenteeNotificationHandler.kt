package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.MenteeRepository
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
    private val coffeeChatRepository: CoffeeChatRepository,
    private val notificationRepository: NotificationRepository,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorCanceledFromMenteeFlowEvent(event: MenteeNotification.MentorCanceledFromMenteeFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_CANCEL,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorRejectedFromMenteeFlowEvent(event: MenteeNotification.MentorRejectedFromMenteeFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_REJECT,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorSuggestedFromMentorFlowEvent(event: MenteeNotification.MentorSuggestedFromMentorFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorCanceledFromMentorFlowEvent(event: MenteeNotification.MentorCanceledFromMentorFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_CANCEL,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleMentorFinallyCanceledFromMentorFlowEvent(event: MenteeNotification.MentorFinallyCanceledFromMentorFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL,
        )
    }

    private fun notify(
        menteeId: Long,
        coffeeChatId: Long,
        type: NotificationType,
    ) {
        val mentee: Mentee = menteeRepository.getById(menteeId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(coffeeChatId)

        notificationRepository.save(
            Notification.create(
                mentee,
                coffeeChat,
                type,
            ),
        )
    }
}
