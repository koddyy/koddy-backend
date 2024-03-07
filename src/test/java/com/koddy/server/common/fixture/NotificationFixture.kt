package com.koddy.server.common.fixture

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.member.domain.model.Member
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.model.NotificationType
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_APPROVE_FROM_MENTEE_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_REJECT_FROM_MENTEE_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_APPLY_FROM_MENTEE_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_REJECT_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTOR_APPROVE_FROM_MENTEE_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW

enum class NotificationFixture(
    val type: NotificationType,
) {
    // 멘토 수신 알림
    멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW(MENTOR_RECEIVE_MENTEE_APPLY_FROM_MENTEE_FLOW),
    멘토_수신_MENTEE_CANCEL_FROM_MENTEE_FLOW(MENTOR_RECEIVE_MENTEE_CANCEL_FROM_MENTEE_FLOW),
    멘토_수신_MENTOR_APPROVE_FROM_MENTEE_FLOW(MENTOR_RECEIVE_MENTOR_APPROVE_FROM_MENTEE_FLOW),

    멘토_수신_MENTEE_CANCEL_FROM_MENTOR_FLOW(MENTOR_RECEIVE_MENTEE_CANCEL_FROM_MENTOR_FLOW),
    멘토_수신_MENTEE_REJECT_FROM_MENTOR_FLOW(MENTOR_RECEIVE_MENTEE_REJECT_FROM_MENTOR_FLOW),
    멘토_수신_MENTEE_PENDING_FROM_MENTOR_FLOW(MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW),
    멘토_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW(MENTOR_RECEIVE_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW),

    // 멘티 수신 알림
    멘티_수신_MENTOR_CANCEL_FROM_MENTEE_FLOW(MENTEE_RECEIVE_MENTOR_CANCEL_FROM_MENTEE_FLOW),
    멘티_수신_MENTOR_REJECT_FROM_MENTEE_FLOW(MENTEE_RECEIVE_MENTOR_REJECT_FROM_MENTEE_FLOW),
    멘티_수신_MENTOR_APPROVE_FROM_MENTEE_FLOW(MENTEE_RECEIVE_MENTOR_APPROVE_FROM_MENTEE_FLOW),

    멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW(MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW),
    멘티_수신_MENTOR_CANCEL_FROM_MENTOR_FLOW(MENTEE_RECEIVE_MENTOR_CANCEL_FROM_MENTOR_FLOW),
    멘티_수신_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW(MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW),
    멘티_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW(MENTEE_RECEIVE_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW),
    ;

    fun toDomain(
        id: Long = 0L,
        target: Member<*>,
        coffeeChat: CoffeeChat,
    ): Notification {
        return Notification(
            id = id,
            target = target,
            coffeeChat = coffeeChat,
            type = this.type,
        )
    }
}
