package com.koddy.server.notification.application.usecase

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.global.query.PageCreator
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.notification.application.usecase.query.GetNotifications
import com.koddy.server.notification.application.usecase.query.response.NotificationSummary
import com.koddy.server.notification.domain.repository.query.NotificationQueryRepository
import com.koddy.server.notification.domain.repository.query.response.NotificationDetails
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

@UseCase
class GetNotificationsUseCase(
    private val notificationQueryRepository: NotificationQueryRepository,
) {
    @KoddyReadOnlyTransactional
    fun invoke(query: GetNotifications): SliceResponse<List<NotificationSummary>> {
        val authenticated: Authenticated = query.authenticated
        val pageable: Pageable = PageCreator.create(query.page)

        val result: Slice<NotificationDetails> = when (authenticated.isMentor) {
            true -> notificationQueryRepository.fetchMentorNotifications(authenticated.id, pageable)
            false -> notificationQueryRepository.fetchMenteeNotifications(authenticated.id, pageable)
        }

        return SliceResponse(
            result = result.content.map { NotificationSummary.from(it) },
            hasNext = result.hasNext(),
        )
    }
}
