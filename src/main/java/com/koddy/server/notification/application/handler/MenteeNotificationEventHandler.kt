package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.global.log.logger
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.service.MemberReader
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.model.NotificationType
import com.koddy.server.notification.domain.repository.NotificationRepository
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MenteeNotificationEventHandler(
    private val memberReader: MemberReader,
    private val coffeeChatReader: CoffeeChatReader,
    private val notificationRepository: NotificationRepository,
) {
    private val log: Logger = logger()

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorCanceledFromMenteeFlowEvent(event: MenteeNotification.MentorCanceledFromMenteeFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_CANCEL_FROM_MENTEE_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorRejectedFromMenteeFlowEvent(event: MenteeNotification.MentorRejectedFromMenteeFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_REJECT_FROM_MENTEE_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorSuggestedFromMentorFlowEvent(event: MenteeNotification.MentorSuggestedFromMentorFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorCanceledFromMentorFlowEvent(event: MenteeNotification.MentorCanceledFromMentorFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_CANCEL_FROM_MENTOR_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMentorFinallyCanceledFromMentorFlowEvent(event: MenteeNotification.MentorFinallyCanceledFromMentorFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW,
        )
    }

    private fun notify(
        event: MenteeNotification,
        type: NotificationType,
    ) {
        log.info(
            "Notify [{}] -> coffeeChat={}, menteeId={}, time={}",
            type.name,
            event.coffeeChatId,
            event.menteeId,
            event.eventPublishedAt,
        )

        val mentee: Mentee = memberReader.getMentee(event.menteeId)
        val coffeeChat: CoffeeChat = coffeeChatReader.getById(event.coffeeChatId)

        notificationRepository.save(
            Notification.create(
                mentee,
                coffeeChat,
                type,
            ),
        )
    }
}
