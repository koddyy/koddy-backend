package com.koddy.server.auth.infrastructure.auth;

import com.koddy.server.auth.domain.model.code.AuthCodeGenerator;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.RedisTest;
import com.koddy.server.global.redis.RedisOperator;
import com.koddy.server.global.redis.StringRedisOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Import({RedisAuthenticationProcessor.class, StringRedisOperator.class})
@DisplayName("Auth -> RedisAuthenticationProcessor 테스트")
class RedisAuthenticationProcessorTest extends RedisTest {
    @Autowired
    private RedisAuthenticationProcessor sut;

    @Autowired
    private RedisOperator<String, String> redisOperator;

    private static final String AUTH_KEY = "MAIL-AUTH:sjiwon4491@gmail.com";
    private static final String AUTH_CODE = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);

    @TestConfiguration
    static class RedisMailAuthenticationProcessorTestConfig {
        @Bean
        public AuthCodeGenerator authCodeGenerator() {
            return () -> AUTH_CODE;
        }
    }

    @Test
    @DisplayName("Redis에 인증번호를 저장한다")
    void storeAuthCode() {
        // when
        final String result = sut.storeAuthCode(AUTH_KEY);

        // then
        assertAll(
                () -> assertThat(result).isEqualTo(AUTH_CODE),
                () -> assertThat(redisOperator.get(AUTH_KEY)).isEqualTo(AUTH_CODE)
        );
    }

    @Test
    @DisplayName("Redis에 저장된 인증번호와 요청 인증번호가 일치하는지 확인한다")
    void verifyAuthCode() {
        // given
        sut.storeAuthCode(AUTH_KEY);

        // when - then
        assertAll(
                () -> assertDoesNotThrow(() -> sut.verifyAuthCode(AUTH_KEY, AUTH_CODE)),
                () -> assertThatThrownBy(() -> sut.verifyAuthCode(AUTH_KEY, AUTH_CODE + "wrong"))
                        .isInstanceOf(AuthException.class)
                        .hasMessage(INVALID_AUTH_CODE.getMessage())
        );
    }

    @Test
    @DisplayName("인증 완료 후 Redis에 저장된 인증번호를 제거한다")
    void deleteAuthCode() {
        // given
        sut.storeAuthCode(AUTH_KEY);
        assertThat(redisOperator.get(AUTH_KEY)).isEqualTo(AUTH_CODE);

        // when
        sut.deleteAuthCode(AUTH_KEY);

        // then
        assertThat(redisOperator.get(AUTH_KEY)).isNull();
    }
}
