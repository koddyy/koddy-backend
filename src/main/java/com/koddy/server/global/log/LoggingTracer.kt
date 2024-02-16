package com.koddy.server.global.log

import lombok.RequiredArgsConstructor
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class LoggingTracer(
    private val loggingStatusManager: LoggingStatusManager,
) {
    private val log: Logger = logger()

    fun methodCall(
        methodSignature: String,
        args: Array<Any?>,
    ) {
        loggingStatusManager.syncStatus()

        val loggingStatus: LoggingStatus = loggingStatusManager.getExistLoggingStatus()
        loggingStatus.increaseDepth()

        if (log.isInfoEnabled) {
            log.info(
                "{} args={}",
                loggingStatus.depthPrefix(REQUEST_PREFIX) + methodSignature,
                args
            )
        }
    }

    fun methodReturn(methodSignature: String) {
        val loggingStatus: LoggingStatus = loggingStatusManager.getExistLoggingStatus()

        if (log.isInfoEnabled) {
            log.info(
                "{} time={}ms",
                loggingStatus.depthPrefix(RESPONSE_PREFIX) + methodSignature,
                loggingStatus.calculateTakenTime()
            )
        }
        loggingStatus.decreaseDepth()
    }

    fun throwException(
        methodSignature: String,
        exception: Throwable,
    ) {
        val loggingStatus: LoggingStatus = loggingStatusManager.getExistLoggingStatus()

        if (log.isInfoEnabled) {
            log.info(
                "{} time={}ms ex={}",
                loggingStatus.depthPrefix(EXCEPTION_PREFIX) + methodSignature,
                loggingStatus.calculateTakenTime(),
                exception.toString()
            )
        }
        loggingStatus.decreaseDepth()
    }

    companion object {
        private const val REQUEST_PREFIX = "--->"
        private const val RESPONSE_PREFIX = "<---"
        private const val EXCEPTION_PREFIX = "<X--"
    }
}
