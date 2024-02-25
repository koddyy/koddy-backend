package com.koddy.server.member.domain.event

import com.koddy.server.global.base.BaseDomainEvent

data class MailAuthenticatedEvent(
    val mentorId: Long,
    val targetEmail: String,
    val authCode: String,
) : BaseDomainEvent()
