package com.koddy.server.member.domain.model;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static com.koddy.server.member.exception.MemberExceptionCode.CURRENT_PASSWORD_IS_NOT_MATCH;
import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_PASSWORD_PATTERN;
import static com.koddy.server.member.exception.MemberExceptionCode.PASSWORD_SAME_AS_BEFORE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> 도메인 [Password VO] 테스트")
class PasswordTest extends ParallelTest {
    public final Encryptor encryptor = getEncryptor();

    @Nested
    @DisplayName("Password 생성")
    class Construct {
        @ParameterizedTest
        @ValueSource(strings = {"", "123", "abc", "!@#", "Tabc12!", "abcdeabcdeabcdeabcde1", "Helloworld123!@#+"})
        @DisplayName("형식에 맞지 않는 패스워드면 예외가 발생한다")
        void throwExceptionByInvalidPasswordPattern(final String value) {
            assertThatThrownBy(() -> Password.encrypt(value, encryptor))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(INVALID_PASSWORD_PATTERN.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"helloworld123!@#", "Helloworld123!@#"})
        @DisplayName("Password를 생성한다")
        void success(final String value) {
            // when
            final Password password = Password.encrypt(value, encryptor);

            // then
            assertAll(
                    () -> assertThat(password.getValue()).isNotEqualTo(value),
                    () -> assertThat(encryptor.isHashMatch(value, password.getValue())).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("Password 수정")
    class Update {
        private final String oldValue = "abcABC123!@#";
        private final String newValue = oldValue + "diff";

        @Test
        @DisplayName("기존 비밀번호 확인 시 일치하지 않으면 수정할 수 없다")
        void throwExceptionByCurrentPasswordIsNotMatch() {
            // given
            final Password password = Password.encrypt(oldValue, encryptor);

            // when - then
            assertThatThrownBy(() -> password.update(oldValue + "diff", newValue, encryptor))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(CURRENT_PASSWORD_IS_NOT_MATCH.getMessage());
        }

        @Test
        @DisplayName("이전과 동일한 Password로 수정하려고 하면 예외가 발생한다")
        void throwExceptionByPasswordSameAsBefore() {
            // given
            final Password password = Password.encrypt(oldValue, encryptor);

            // when - then
            assertThatThrownBy(() -> password.update(oldValue, oldValue, encryptor))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(PASSWORD_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("Password를 수정한다")
        void success() {
            // given
            final Password oldPassword = Password.encrypt(oldValue, encryptor);

            // when
            final Password newPassword = oldPassword.update(oldValue, newValue, encryptor);

            // then
            assertAll(
                    () -> assertThat(encryptor.isHashMatch(oldValue, newPassword.getValue())).isFalse(),
                    () -> assertThat(encryptor.isHashMatch(newValue, newPassword.getValue())).isTrue()
            );
        }
    }
}
