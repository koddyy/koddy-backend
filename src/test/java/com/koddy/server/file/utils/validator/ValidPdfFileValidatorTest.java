package com.koddy.server.file.utils.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("File -> ValidPdfFileValidator 테스트")
class ValidPdfFileValidatorTest {
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final ValidPdfFileValidator sut = new ValidPdfFileValidator();

    @Test
    @DisplayName("PDF 파일이 아니면 Validator를 통과하지 못한다")
    void notPdf() {
        // when
        final boolean actual1 = sut.isValid("hello.jpg", context);
        final boolean actual2 = sut.isValid("hello.jpeg", context);
        final boolean actual3 = sut.isValid("hello.png", context);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isFalse(),
                () -> assertThat(actual3).isFalse()
        );
    }

    @Test
    @DisplayName("PDF 파일이면 Validator를 통과한다")
    void pdf() {
        assertThat(sut.isValid("hello.pdf", context)).isTrue();
    }
}
