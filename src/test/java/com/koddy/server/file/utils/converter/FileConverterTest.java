package com.koddy.server.file.utils.converter;

import com.koddy.server.common.UnitTest;
import com.koddy.server.file.domain.model.RawFileData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static com.koddy.server.common.utils.FileMockingUtils.createFile;
import static com.koddy.server.file.domain.model.FileExtension.PNG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("File -> FileConverter 테스트")
class FileConverterTest extends UnitTest {
    @Nested
    @DisplayName("파일 Convert")
    class ConvertFile {
        @Test
        @DisplayName("MultipartFile이 비어있으면 null이 반환된다")
        void returnNull() {
            // given
            final MultipartFile file = new MockMultipartFile("cat.png", new byte[0]);

            // when
            final RawFileData result = FileConverter.convertFile(file);

            // when - then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("MultipartFile -> RawFileData로 Converting한다")
        void success() {
            // given
            final MultipartFile file = createFile("cat.png", "image/png");

            // when
            final RawFileData result = FileConverter.convertFile(file);

            // then
            assertAll(
                    () -> assertThat(result.fileName()).isEqualTo("cat.png"),
                    () -> assertThat(result.contentType()).isEqualTo("image/png"),
                    () -> assertThat(result.extension()).isEqualTo(PNG)
            );
        }
    }
}
