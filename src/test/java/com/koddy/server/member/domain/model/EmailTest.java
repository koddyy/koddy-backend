package com.koddy.server.member.domain.model;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.koddy.server.member.domain.model.EmailStatus.ACTIVE;
import static com.koddy.server.member.domain.model.EmailStatus.INACTIVE;
import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_EMAIL_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member -> 도메인 [Email VO] 테스트")
class EmailTest extends ParallelTest {
    @Nested
    @DisplayName("Email 생성")
    class Construct {
        @ParameterizedTest
        @ValueSource(strings = {"", "abc", "@gmail.com", "abc@gmail", "abc@gmail."})
        @DisplayName("형식에 맞지 않는 Email이면 예외가 발생한다")
        void throwExceptionByInvalidEmailPattern(final String value) {
            assertThatThrownBy(() -> Email.init(value))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(INVALID_EMAIL_PATTERN.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"sjiwon4491@gmail.com", "sjiwon4491@naver.com", "sjiwon@kyonggi.ac.kr"})
        @DisplayName("Email을 생성한다")
        void construct(final String value) {
            assertDoesNotThrow(() -> Email.init(value));
        }
    }

    @Test
    @DisplayName("이메일 인증이 완료되었는지 확인한다")
    void isAuthenticated() {
        // given
        final Email email = Email.init("sjiwon4491@gmail.com");

        /* INACTIVE */
        assertAll(
                () -> assertThat(email.isAuthenticated()).isFalse(),
                () -> assertThat(email.getStatus()).isEqualTo(INACTIVE)
        );

        /* ACTIVE */
        email.activate();
        assertAll(
                () -> assertThat(email.isAuthenticated()).isTrue(),
                () -> assertThat(email.getStatus()).isEqualTo(ACTIVE)
        );
    }
}
