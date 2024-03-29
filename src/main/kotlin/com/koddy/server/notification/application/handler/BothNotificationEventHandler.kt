package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.BothNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.global.log.logger
import com.koddy.server.member.domain.model.mentee.Mentee
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
class BothNotificationEventHandler(
    private val memberReader: MemberReader,
    private val coffeeChatReader: CoffeeChatReader,
    private val notificationRepository: NotificationRepository,
) {
    private val log: Logger = logger()

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleApprovedFromMenteeFlowEvent(event: BothNotification.ApprovedFromMenteeFlowEvent) {
        notify(
            event = event,
            mentorNotifyType = NotificationType.MENTOR_RECEIVE_MENTOR_APPROVE_FROM_MENTEE_FLOW,
            menteeNotifyType = NotificationType.MENTEE_RECEIVE_MENTOR_APPROVE_FROM_MENTEE_FLOW,
        )
    }

    @Async("eventAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleFinallyApprovedFromMentorFlowEvent(event: BothNotification.FinallyApprovedFromMentorFlowEvent) {
        notify(
            event = event,
            mentorNotifyType = NotificationType.MENTOR_RECEIVE_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW,
            menteeNotifyType = NotificationType.MENTEE_RECEIVE_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW,
        )
    }

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

        val mentor: Mentor = memberReader.getMentor(event.mentorId)
        val mentee: Mentee = memberReader.getMentee(event.menteeId)
        val coffeeChat: CoffeeChat = coffeeChatReader.getById(event.coffeeChatId)

        notificationRepository.saveAll(
            listOf(
                Notification(
                    target = mentor,
                    coffeeChat = coffeeChat,
                    type = mentorNotifyType,
                ),
                Notification(
                    target = mentee,
                    coffeeChat = coffeeChat,
                    type = menteeNotifyType,
                ),
            ),
        )
    }
}
