package com.koddy.server.file.infrastructure.s3;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.file.utils.converter.FileConverter;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.koddy.server.common.utils.FileMockingUtils.createFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("File -> S3FileUploader 테스트")
class S3FileUploaderTest extends ParallelTest {
    private final S3Template s3Template = mock(S3Template.class);
    private final S3Resource s3Resource = mock(S3Resource.class);
    private final S3FileUploader sut = new S3FileUploader(s3Template, "bucket");

    @Test
    @DisplayName("Client가 파일을 전송하지 않았으면 EMPTY URL을 반환한다")
    void returnEmptyUrlWhenFileIsNullOrEmpty() {
        // when
        final String uploadUrl = sut.upload(null);

        // then
        assertAll(
                () -> verify(s3Template, times(0)).upload(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class)),
                () -> assertThat(uploadUrl).isEqualTo("EMPTY")
        );
    }

    @Test
    @DisplayName("S3에 파일을 업로드한다")
    void success() throws IOException {
        // given
        final URL mockUrl = new URL("https://s3/cat.png");
        given(s3Template.upload(
                any(String.class),
                any(String.class),
                any(InputStream.class),
                any(ObjectMetadata.class)
        )).willReturn(s3Resource);
        given(s3Resource.getURL()).willReturn(mockUrl);

        // when
        final String uploadUrl = sut.upload(FileConverter.convertFile(createFile("cat.png", "image/png")));

        // then
        assertAll(
                () -> verify(s3Template, times(1)).upload(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class)),
                () -> assertThat(uploadUrl).isEqualTo("https://s3/cat.png")
        );
    }
}
