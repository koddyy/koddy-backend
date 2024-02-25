package com.koddy.server.global.base

import java.time.LocalDateTime

abstract class BaseDomainEvent(
    val eventPublishedAt: LocalDateTime = LocalDateTime.now(),
)
