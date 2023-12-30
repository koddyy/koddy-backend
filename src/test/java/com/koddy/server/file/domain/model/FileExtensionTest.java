package com.koddy.server.file.domain.model;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.file.exception.FileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.koddy.server.file.domain.model.FileExtension.JPEG;
import static com.koddy.server.file.domain.model.FileExtension.JPG;
import static com.koddy.server.file.domain.model.FileExtension.PNG;
import static com.koddy.server.file.exception.FileExceptionCode.INVALID_FILE_EXTENSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("File -> 도메인 [FileExtension] 테스트")
class FileExtensionTest extends ParallelTest {
    @Nested
    @DisplayName("파일 확장자 추출")
    class GetExtensionFromFileName {
        @ParameterizedTest
        @ValueSource(strings = {"hello.gif", "hello.mp3", "hello.xls", "hello.alz"})
        @DisplayName("제공하지 않는 파일의 확장자면 예외가 발생한다")
        void throwExceptionByInvalidFileExtension(final String fileName) {
            assertThatThrownBy(() -> FileExtension.getExtensionViaFimeName(fileName))
                    .isInstanceOf(FileException.class)
                    .hasMessage(INVALID_FILE_EXTENSION.getMessage());
        }

        @ParameterizedTest
        @MethodSource("validExtension")
        @DisplayName("파일 확장자에 대한 FileExtension을 얻는다")
        void success(final String fileName, final FileExtension extension) {
            assertThat(FileExtension.getExtensionViaFimeName(fileName)).isEqualTo(extension);
        }

        private static Stream<Arguments> validExtension() {
            return Stream.of(
                    Arguments.of("hello.jpg", JPG),
                    Arguments.of("hello.jpeg", JPEG),
                    Arguments.of("hello.png", PNG)
            );
        }
    }
}
