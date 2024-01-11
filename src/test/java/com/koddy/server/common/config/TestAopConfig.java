package com.koddy.server.common.config;

import com.koddy.server.global.aop.OnlyMenteeAop;
import com.koddy.server.global.aop.OnlyMentorAop;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@EnableAspectJAutoProxy
public class TestAopConfig {
    @Bean
    public OnlyMentorAop onlyMentorAop() {
        return new OnlyMentorAop();
    }

    @Bean
    public OnlyMenteeAop onlyMenteeAop() {
        return new OnlyMenteeAop();
    }
}
