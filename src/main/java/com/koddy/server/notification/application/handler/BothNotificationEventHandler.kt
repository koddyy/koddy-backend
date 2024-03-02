package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.BothNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.global.log.logger
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository
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
class BothNotificationEventHandler(
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
    private val notificationRepository: NotificationRepository,
) {
    private val log: Logger = logger()

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleApprovedFromMenteeFlowEvent(event: BothNotification.ApprovedFromMenteeFlowEvent) =
        notify(
            event = event,
            mentorNotifyType = NotificationType.MENTOR_RECEIVE_MENTOR_APPROVE_FROM_MENTEE_FLOW,
            menteeNotifyType = NotificationType.MENTEE_RECEIVE_MENTOR_APPROVE_FROM_MENTEE_FLOW,
        )

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleFinallyApprovedFromMentorFlowEvent(event: BothNotification.FinallyApprovedFromMentorFlowEvent) =
        notify(
            event = event,
            mentorNotifyType = NotificationType.MENTOR_RECEIVE_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW,
            menteeNotifyType = NotificationType.MENTEE_RECEIVE_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW,
        )

    private fun notify(
        event: BothNotification,
        mentorNotifyType: NotificationType,
        menteeNotifyType: NotificationType,
    ) {
        log.info(
            "Notify [{}] -> coffeeChat={}, mentorId={}, time={}",
            mentorNotifyType.name,
            event.coffeeChatId,
            event.mentorId,
            event.eventPublishedAt,
        )
        log.info(
            "Notify [{}] -> coffeeChat={}, menteeId={}, time={}",
            menteeNotifyType.name,
            event.coffeeChatId,
            event.menteeId,
            event.eventPublishedAt,
        )

        val mentor: Mentor = mentorRepository.getById(event.mentorId)
        val mentee: Mentee = menteeRepository.getById(event.menteeId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(event.coffeeChatId)

        notificationRepository.saveAll(
            listOf(
                Notification.create(
                    mentor,
                    coffeeChat,
                    mentorNotifyType,
                ),
                Notification.create(
                    mentee,
                    coffeeChat,
                    menteeNotifyType,
                ),
            ),
        )
    }
}
