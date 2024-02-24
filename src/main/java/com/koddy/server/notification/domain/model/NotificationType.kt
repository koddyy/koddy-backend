package com.koddy.server.notification.domain.model

import com.koddy.server.coffeechat.domain.model.Reason
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.notification.domain.model.NotificationType.Category.CANCEL_REASON
import com.koddy.server.notification.domain.model.NotificationType.Category.REJECT_REASON
import com.koddy.server.notification.domain.model.NotificationType.Category.SCHEDULED
import com.koddy.server.notification.domain.model.NotificationType.Category.SIMPLE
import java.time.format.DateTimeFormatter

enum class NotificationType(
    val form: String,
    val category: Category,
) {
    // 멘토가 받는 알림
    MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY("%s님이 커피챗을 신청했습니다.", SIMPLE),
    MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", CANCEL_REASON),
    MENTOR_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE("%s님과의 커피챗이 %s로 예정되었습니다.", SCHEDULED),

    MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", CANCEL_REASON),
    MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_REJECT("%s님이 커피챗을 거절했습니다. (거절 사유: %s)", REJECT_REASON),
    MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING("%s님이 커피챗을 수락했습니다.", SIMPLE),
    MENTOR_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE("%s님과의 커피챗이 %s로 예정되었습니다.", SCHEDULED),

    // 멘티가 받는 알림
    MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", CANCEL_REASON),
    MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_REJECT("%s님이 커피챗을 거절했습니다. (거절 사유: %s)", REJECT_REASON),
    MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE("%s님과의 커피챗이 %s로 예정되었습니다.", SCHEDULED),

    MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST("%s님이 커피챗을 제안했습니다.", SIMPLE),
    MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", CANCEL_REASON),
    MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", CANCEL_REASON),
    MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE("%s님과의 커피챗이 %s로 예정되었습니다.", SCHEDULED),
    ;

    fun createMentorNotification(
        menteeName: String,
        reason: Reason,
        reservation: Reservation,
    ): String =
        when (category) {
            SIMPLE -> complete(menteeName)
            CANCEL_REASON -> complete(menteeName, reason.cancelReason)
            REJECT_REASON -> complete(menteeName, reason.rejectReason)
            SCHEDULED -> complete(menteeName, reservation.start.toLocalDate().format(dateTimeFormatter))
        }

    fun createMenteeNotification(
        mentorName: String,
        reason: Reason,
        reservation: Reservation,
    ): String =
        when (category) {
            SIMPLE -> complete(mentorName)
            CANCEL_REASON -> complete(mentorName, reason.cancelReason)
            REJECT_REASON -> complete(mentorName, reason.rejectReason)
            SCHEDULED -> complete(mentorName, reservation.start.toLocalDate().format(dateTimeFormatter))
        }

    private fun complete(vararg arguments: Any): String = String.format(form, *arguments)

    enum class Category {
        SIMPLE,
        CANCEL_REASON,
        REJECT_REASON,
        SCHEDULED,
    }

    companion object {
        private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    }
}
