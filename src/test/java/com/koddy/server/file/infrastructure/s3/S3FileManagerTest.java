package com.koddy.server.file.infrastructure.s3;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.file.domain.model.PresignedFileData;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
import com.koddy.server.file.exception.FileException;
import com.koddy.server.file.utils.converter.FileConverter;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.koddy.server.common.utils.FileMockingUtils.createFile;
import static com.koddy.server.file.exception.FileExceptionCode.FILE_NOT_UPLOADED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("File -> S3FileUploader 테스트")
class S3FileManagerTest extends ParallelTest {
    private final S3Template s3Template = mock(S3Template.class);
    private final S3Resource s3Resource = mock(S3Resource.class);
    private final S3FileManager sut = new S3FileManager(s3Template, "bucket");

    @Nested
    @DisplayName("Presigned Url 생성")
    class CreatePresignedUrl {
        @Test
        @DisplayName("FileName, ContentType을 통해서 Presigned Url을 얻는다")
        void success() throws IOException {
            // given
            final URL url = new URL("https://s3-presigned-url/profiles/cat.png");
            given(s3Template.createSignedPutURL(anyString(), anyString(), any())).willReturn(url);

            // when
            final PresignedUrlDetails presignedUrl = sut.createPresignedUrl(new PresignedFileData("cat.png"));

            // then
            assertAll(
                    () -> verify(s3Template, times(1)).createSignedPutURL(anyString(), anyString(), any()),
                    () -> assertThat(presignedUrl.preSignedUrl()).isEqualTo("https://s3-presigned-url/profiles/cat.png"),
                    () -> assertThat(presignedUrl.uploadFileUrl()).contains("https://s3-presigned-url/profiles/", ".png")
            );
        }
    }

    @Nested
    @DisplayName("파일 업로드")
    class Upload {
        @Test
        @DisplayName("Client가 파일을 전송하지 않았으면 예외가 발생한다")
        void returnEmptyUrlWhenFileIsNullOrEmpty() {
            assertThatThrownBy(() -> sut.upload(null))
                    .isInstanceOf(FileException.class)
                    .hasMessage(FILE_NOT_UPLOADED.getMessage());

            verify(s3Template, times(0)).upload(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class));
        }

        @Test
        @DisplayName("S3에 파일을 업로드한다")
        void success() throws IOException {
            // given
            final URL url = new URL("https://s3/cat.png");
            given(s3Template.upload(
                    any(String.class),
                    any(String.class),
                    any(InputStream.class),
                    any(ObjectMetadata.class)
            )).willReturn(s3Resource);
            given(s3Resource.getURL()).willReturn(url);

            // when
            final String uploadUrl = sut.upload(FileConverter.convertFile(createFile("cat.png", "image/png")));

            // then
            assertAll(
                    () -> verify(s3Template, times(1)).upload(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class)),
                    () -> assertThat(uploadUrl).isEqualTo("https://s3/cat.png")
            );
        }
    }
}
