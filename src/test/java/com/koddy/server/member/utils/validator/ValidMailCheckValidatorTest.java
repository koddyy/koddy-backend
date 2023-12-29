package com.koddy.server.member.utils.validator;

import com.koddy.server.common.ParallelTest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> ValidMailCheckValidator 테스트")
class ValidMailCheckValidatorTest extends ParallelTest {
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    private final ValidMailCheckValidator sut = new ValidMailCheckValidator();

    @Test
    @DisplayName("이메일 중복 확인 결과를 null로 보내면 Validator를 통과할 수 없다")
    void nullChecked() {
        // given
        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
        given(builder.addConstraintViolation()).willReturn(context);

        // when
        final boolean actual = sut.isValid(null, context);

        // then
        assertAll(
                () -> verify(context, times(1)).disableDefaultConstraintViolation(),
                () -> verify(context, times(1)).buildConstraintViolationWithTemplate("이메일 중복 확인 결과는 필수입니다."),
                () -> verify(builder, times(1)).addConstraintViolation(),
                () -> assertThat(actual).isFalse()
        );
    }

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
