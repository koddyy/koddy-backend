package com.koddy.server.global.log

import org.springframework.stereotype.Component

@Component
class LoggingStatusManager {
    private val statusContainer = ThreadLocal<LoggingStatus>()

    fun getExistLoggingStatus(): LoggingStatus =
        statusContainer.get()
            ?: throw IllegalStateException("ThreadLocal LoggingStatus not exists...")

    fun syncStatus() {
        val status: LoggingStatus? = statusContainer.get()
        if (status == null) {
            statusContainer.set(LoggingStatus())
        }
    }

    fun clearResource() = statusContainer.remove()
}
