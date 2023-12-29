package com.koddy.server.member.utils;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Member -> ValidMailCheckValidator 테스트")
class ValidMailCheckValidatorTest {
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final ValidMailCheckValidator sut = new ValidMailCheckValidator();

    @Test
    @DisplayName("이메일 중복 확인을 하지 않았으면 Validator를 통과할 수 없다")
    void unChecked() {
        // when
        final boolean actual = sut.isValid(false, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("이메일 중복 확인을 진행했으면 Validator를 통과한다")
    void checked() {
        // when
        final boolean actual = sut.isValid(true, context);

        // then
        assertThat(actual).isTrue();
    }
}
