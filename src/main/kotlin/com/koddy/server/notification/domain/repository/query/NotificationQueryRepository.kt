package com.koddy.server.notification.domain.repository.query

import com.koddy.server.notification.domain.repository.query.response.NotificationDetails
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface NotificationQueryRepository {
    fun fetchMentorNotifications(
        mentorId: Long,
        pageable: Pageable,
    ): Slice<NotificationDetails>

    fun fetchMenteeNotifications(
        menteeId: Long,
        pageable: Pageable,
    ): Slice<NotificationDetails>
}
