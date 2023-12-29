package com.koddy.server.auth.infrastructure;

import com.koddy.server.auth.domain.model.code.AuthCodeGenerator;
import com.koddy.server.auth.domain.model.code.AuthKey;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.RedisTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Import(RedisAuthenticationProcessor.class)
@DisplayName("Auth -> RedisAuthenticationProcessor 테스트")
class RedisAuthenticationProcessorTest extends RedisTest {
    @Autowired
    private RedisAuthenticationProcessor sut;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> operations;

    private static final String EMAIL = "sjiwon4491@gmail.com";
    private static final String AUTH_CODE = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);

    @TestConfiguration
    static class RedisMailAuthenticationProcessorTestConfig {
        @Bean
        public AuthCodeGenerator authCodeGenerator() {
            return () -> AUTH_CODE;
        }
    }

    @BeforeEach
    void setUp() {
        operations = redisTemplate.opsForValue();
    }

    @Test
    @DisplayName("Redis에 인증번호를 저장한다")
    void storeAuthCode() {
        // given
        final String key = AuthKey.EMAIL.generateAuthKey(EMAIL);

        // when
        final String result = sut.storeAuthCode(key);

        // then
        assertAll(
                () -> assertThat(result).isEqualTo(AUTH_CODE),
                () -> assertThat(operations.get(key)).isEqualTo(AUTH_CODE)
        );
    }

    @Test
    @DisplayName("Redis에 저장된 인증번호와 요청 인증번호가 일치하는지 확인한다")
    void verifyAuthCode() {
        // given
        final String key = AuthKey.EMAIL.generateAuthKey(EMAIL);
        final String wrong = "fake...";

        sut.storeAuthCode(key);

        // when - then
        assertAll(
                () -> assertDoesNotThrow(() -> sut.verifyAuthCode(key, AUTH_CODE)),
                () -> assertThatThrownBy(() -> sut.verifyAuthCode(key, wrong))
                        .isInstanceOf(AuthException.class)
                        .hasMessage(INVALID_AUTH_CODE.getMessage())
        );
    }

    @Test
    @DisplayName("인증 완료 후 Redis에 저장된 인증번호를 제거한다")
    void deleteAuthCode() {
        // given
        final String key = AuthKey.EMAIL.generateAuthKey(EMAIL);

        sut.storeAuthCode(key);
        assertThat(operations.get(key)).isEqualTo(AUTH_CODE);

        // when
        sut.deleteAuthCode(key);

        // then
        assertThat(operations.get(key)).isNull();
    }
}
