package com.koddy.server.notification.application.handler

import com.koddy.server.coffeechat.domain.event.BothNotification
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
class BothNotificationHandler(
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
    private val notificationRepository: NotificationRepository,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleApprovedFromMenteeFlowEvent(event: BothNotification.ApprovedFromMenteeFlowEvent) {
        notify(
            mentorId = event.mentorId,
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            mentorNotifyType = NotificationType.MENTOR_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE,
            menteeNotifyType = NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleFinallyApprovedFromMentorFlowEvent(event: BothNotification.FinallyApprovedFromMentorFlowEvent) {
        notify(
            mentorId = event.mentorId,
            menteeId = event.menteeId,
            coffeeChatId = event.coffeeChatId,
            mentorNotifyType = NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE,
            menteeNotifyType = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE,
        )
    }

    private fun notify(
        mentorId: Long,
        menteeId: Long,
        coffeeChatId: Long,
        mentorNotifyType: NotificationType,
        menteeNotifyType: NotificationType,
    ) {
        val mentor: Mentor = mentorRepository.getById(mentorId)
        val mentee: Mentee = menteeRepository.getById(menteeId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(coffeeChatId)

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
