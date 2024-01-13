package com.koddy.server.common.config;

import com.koddy.server.global.aop.AccessControlAop;
import com.koddy.server.global.log.LoggingStatusManager;
import com.koddy.server.global.log.LoggingTracer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@EnableAspectJAutoProxy
public class TestAopConfig {
    @Bean
    public AccessControlAop accessControlAop() {
        return new AccessControlAop();
    }

    @Bean
    public LoggingStatusManager loggingStatusManager() {
        return new LoggingStatusManager();
    }

    @Bean
    public LoggingTracer loggingTracer() {
        return new LoggingTracer(loggingStatusManager());
    }
}
