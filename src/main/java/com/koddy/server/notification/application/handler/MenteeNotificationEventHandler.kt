package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.model.NotificationType
import com.koddy.server.notification.domain.repository.NotificationRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MenteeNotificationEventHandler(
    private val menteeRepository: MenteeRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
    private val notificationRepository: NotificationRepository,
) {
    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorCanceledFromMenteeFlowEvent(event: MenteeNotification.MentorCanceledFromMenteeFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_CANCEL,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorRejectedFromMenteeFlowEvent(event: MenteeNotification.MentorRejectedFromMenteeFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_REJECT,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorSuggestedFromMentorFlowEvent(event: MenteeNotification.MentorSuggestedFromMentorFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorCanceledFromMentorFlowEvent(event: MenteeNotification.MentorCanceledFromMentorFlowEvent) {
        notify(
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_CANCEL,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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
