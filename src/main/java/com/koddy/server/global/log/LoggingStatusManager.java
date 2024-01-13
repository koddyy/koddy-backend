package com.koddy.server.global.log;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import static com.koddy.server.global.filter.MdcKey.REQUEST_ID;

@Component
public class LoggingStatusManager {
    private final ThreadLocal<LoggingStatus> statusContainer = new ThreadLocal<>();

    public void syncStatus() {
        final LoggingStatus status = statusContainer.get();
        if (status == null) {
            final LoggingStatus firstLoggingStatus = new LoggingStatus(MDC.get(REQUEST_ID.name()));
            statusContainer.set(firstLoggingStatus);
        }
    }

    public LoggingStatus getExistLoggingStatus() {
        final LoggingStatus status = statusContainer.get();
        if (status == null) {
            throw new IllegalStateException("ThreadLocal LoggingStatus not exists...");
        }
        return status;
    }

    public void increaseDepth() {
        statusContainer.get().increaseDepth();
    }

    public void decreaseDepth() {
        statusContainer.get().decreaseDepth();
    }

    public void clearResource() {
        statusContainer.remove();
    }
}
