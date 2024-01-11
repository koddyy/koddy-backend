package com.koddy.server.common.config;

import com.koddy.server.auth.domain.model.code.AuthCodeGenerator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class BlackboxLogicControlConfig {
    public static final String AUTH_CODE = "123456";

    @Bean
    @Primary
    public AuthCodeGenerator authCodeGenerator() {
        return () -> "123456";
    }
}
