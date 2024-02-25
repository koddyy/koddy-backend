package com.koddy.server.notification.application.usecase.query

import com.koddy.server.auth.domain.model.Authenticated

data class GetNotifications(
    val authenticated: Authenticated,
    val page: Int,
)
