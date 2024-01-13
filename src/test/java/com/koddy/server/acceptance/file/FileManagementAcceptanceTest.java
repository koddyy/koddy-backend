package com.koddy.server.acceptance.file;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.koddy.server.acceptance.file.FileAcceptanceStep.PDF_파일_업로드에_대한_Presigned_Url을_응답받는다;
import static com.koddy.server.acceptance.file.FileAcceptanceStep.이미지_업로드에_대한_Presigned_Url을_응답받는다;
import static com.koddy.server.acceptance.file.FileAcceptanceStep.파일을_업로드한다;
import static com.koddy.server.common.config.BlackboxLogicControlConfig.BUCKET_UPLOAD_PREFIX;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.global.exception.GlobalExceptionCode.VALIDATION_ERROR;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 파일 관련 기능")
public class FileManagementAcceptanceTest extends AcceptanceTest {
    private static final String BUCKET_URL_PREFIX = "https://koddy-upload.s3.amazonaws.com/profiles/";

    @Nested
    @DisplayName("파일 업로드 API")
    class Upload {
        @Test
        @DisplayName("파일을 업로드한다")
        void success() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            파일을_업로드한다("cat.png", accessToken)
                    .statusCode(OK.value());
        }
    }

    @Nested
    @DisplayName("이미지 업로드에 대한 PresignedUrl 응답 API")
    class GetImagePresignedUrl {
        @Test
        @DisplayName("파일 형식이 [JPG, JPEG, PNG] 아니면 예외가 발생한다")
        void throwExceptionByInvalidExtension() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            이미지_업로드에_대한_Presigned_Url을_응답받는다("cat.pdf", accessToken)
                    .statusCode(BAD_REQUEST.value())
                    .body("errorCode", is(VALIDATION_ERROR.getErrorCode()))
                    .body("message", is("이미지 파일[JPG, JPEG, PNG]을 업로드해주세요."));
        }

        @Test
        @DisplayName("이미지 업로드에 대한 PresignedUrl을 응답받는다")
        void success() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            이미지_업로드에_대한_Presigned_Url을_응답받는다("cat.png", accessToken)
                    .statusCode(OK.value())
                    .body("preSignedUrl", startsWith(BUCKET_URL_PREFIX + BUCKET_UPLOAD_PREFIX + "cat.png"))
                    .body("uploadFileUrl", is(BUCKET_URL_PREFIX + BUCKET_UPLOAD_PREFIX + "cat.png"));
        }
    }

    @Nested
    @DisplayName("PDF 파일 업로드에 대한 PresignedUrl 응답 API")
    class GetPdfPresignedUrl {
        @Test
        @DisplayName("파일 형식이 PDF가 아니면 예외가 발생한다")
        void throwExceptionByInvalidExtension() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            PDF_파일_업로드에_대한_Presigned_Url을_응답받는다("cat.png", accessToken)
                    .statusCode(BAD_REQUEST.value())
                    .body("errorCode", is(VALIDATION_ERROR.getErrorCode()))
                    .body("message", is("PDF 파일을 업로드해주세요."));
        }

        @Test
        @DisplayName("PDF 파일 업로드에 대한 PresignedUrl을 응답받는다")
        void success() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            PDF_파일_업로드에_대한_Presigned_Url을_응답받는다("cat.pdf", accessToken)
                    .statusCode(OK.value())
                    .body("preSignedUrl", startsWith(BUCKET_URL_PREFIX + BUCKET_UPLOAD_PREFIX + "cat.pdf"))
                    .body("uploadFileUrl", is(BUCKET_URL_PREFIX + BUCKET_UPLOAD_PREFIX + "cat.pdf"));
        }
    }
}
