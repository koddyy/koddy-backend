package com.koddy.server.common.config;

import com.koddy.server.global.aop.AccessControlAop;
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
}
