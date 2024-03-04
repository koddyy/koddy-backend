package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.MentorNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.global.log.logger
import com.koddy.server.member.domain.model.mentor.Mentor
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
class MentorNotificationEventHandler(
    private val memberReader: MemberReader,
    private val coffeeChatReader: CoffeeChatReader,
    private val notificationRepository: NotificationRepository,
) {
    private val log: Logger = logger()

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMenteeAppliedFromMenteeFlowEvent(event: MentorNotification.MenteeAppliedFromMenteeFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTOR_RECEIVE_MENTEE_APPLY_FROM_MENTEE_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMenteeCanceledFromMenteeFlowEvent(event: MentorNotification.MenteeCanceledFromMenteeFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTOR_RECEIVE_MENTEE_CANCEL_FROM_MENTEE_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMenteeCanceledFromMentorFlowEvent(event: MentorNotification.MenteeCanceledFromMentorFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTOR_RECEIVE_MENTEE_CANCEL_FROM_MENTOR_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMenteeRejectedFromMentorFlowEvent(event: MentorNotification.MenteeRejectedFromMentorFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTOR_RECEIVE_MENTEE_REJECT_FROM_MENTOR_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMenteePendedFromMentorFlowEvent(event: MentorNotification.MenteePendedFromMentorFlowEvent) {
        notify(
            event = event,
            type = NotificationType.MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW,
        )
    }

    private fun notify(
        event: MentorNotification,
        type: NotificationType,
    ) {
        log.info(
            "Notify [{}] -> coffeeChat={}, mentorId={}, time={}",
            type.name,
            event.coffeeChatId,
            event.mentorId,
            event.eventPublishedAt,
        )

        val mentor: Mentor = memberReader.getMentor(event.mentorId)
        val coffeeChat: CoffeeChat = coffeeChatReader.getById(event.coffeeChatId)

        notificationRepository.save(
            Notification.create(
                mentor,
                coffeeChat,
                type,
            ),
        )
    }
}
