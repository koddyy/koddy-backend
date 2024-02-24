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
        val mentor: Mentor = mentorRepository.getById(event.mentorId)
        val mentee: Mentee = menteeRepository.getById(event.menteeId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(event.coffeeChatId)

        val mentorNotifyType: NotificationType = NotificationType.MENTOR_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE
        val menteeNotifyType: NotificationType = NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE
        notificationRepository.saveAll(
            listOf(
                Notification.create(
                    mentor,
                    coffeeChat,
                    mentorNotifyType,
                    mentorNotifyType.createMentorNotification(
                        menteeName = mentee.name,
                        reason = coffeeChat.reason,
                        reservation = coffeeChat.reservation,
                    ),
                ),
                Notification.create(
                    mentee,
                    coffeeChat,
                    menteeNotifyType,
                    menteeNotifyType.createMenteeNotification(
                        mentorName = mentor.name,
                        reason = coffeeChat.reason,
                        reservation = coffeeChat.reservation,
                    ),
                ),
            ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    fun handleFinallyApprovedFromMentorFlowEvent(event: BothNotification.FinallyApprovedFromMentorFlowEvent) {
        val mentor: Mentor = mentorRepository.getById(event.mentorId)
        val mentee: Mentee = menteeRepository.getById(event.menteeId)
        val coffeeChat: CoffeeChat = coffeeChatRepository.getById(event.coffeeChatId)

        val mentorNotifyType: NotificationType = NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE
        val menteeNotifyType: NotificationType = NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE
        notificationRepository.saveAll(
            listOf(
                Notification.create(
                    mentor,
                    coffeeChat,
                    mentorNotifyType,
                    mentorNotifyType.createMentorNotification(
                        menteeName = mentee.name,
                        reason = coffeeChat.reason,
                        reservation = coffeeChat.reservation,
                    ),
                ),
                Notification.create(
                    mentee,
                    coffeeChat,
                    menteeNotifyType,
                    menteeNotifyType.createMenteeNotification(
                        mentorName = mentor.name,
                        reason = coffeeChat.reason,
                        reservation = coffeeChat.reservation,
                    ),
                ),
            ),
        )
    }
}
