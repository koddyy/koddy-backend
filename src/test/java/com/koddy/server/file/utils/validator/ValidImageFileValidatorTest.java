package com.koddy.server.file.utils.validator;

import com.koddy.server.common.UnitTest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("File -> ValidImageFileValidator 테스트")
class ValidImageFileValidatorTest extends UnitTest {
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final ValidImageFileValidator sut = new ValidImageFileValidator();

    @Test
    @DisplayName("이미지 파일이 아니면 Validator를 통과하지 못한다")
    void notImage() {
        // when
        final boolean actual1 = sut.isValid("hello.pdf", context);
        final boolean actual2 = sut.isValid("hello.xls", context);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("이미지 파일이면 Validator를 통과한다")
    void image() {
        // when
        final boolean actual1 = sut.isValid("hello.jpg", context);
        final boolean actual2 = sut.isValid("hello.jpeg", context);
        final boolean actual3 = sut.isValid("hello.png", context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue()
        );
    }
}
