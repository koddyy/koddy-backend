package com.koddy.server.acceptance.file

import com.koddy.server.acceptance.file.FileAcceptanceStep.PDF_파일_업로드에_대한_Presigned_Url을_응답받는다
import com.koddy.server.acceptance.file.FileAcceptanceStep.이미지_업로드에_대한_Presigned_Url을_응답받는다
import com.koddy.server.acceptance.file.FileAcceptanceStep.파일을_업로드한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.config.BlackboxLogicControlConfig
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.global.exception.GlobalExceptionCode.VALIDATION_ERROR
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 파일 관련 기능")
internal class FileManagementAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private const val BUCKET_URL_PREFIX = "https://koddy-upload.s3.amazonaws.com/profiles/"
    }

    @Nested
    @DisplayName("파일 업로드 API")
    internal inner class Upload {
        @Test
        fun `파일을 업로드한다`() {
            // given
            val member: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()

            // when - then
            파일을_업로드한다(
                fileName = "cat.png",
                accessToken = member.token.accessToken,
            ).statusCode(OK.value())
        }
    }

    @Nested
    @DisplayName("이미지 업로드에 대한 PresignedUrl 응답 API")
    internal inner class GetImagePresignedUrl {
        @Test
        fun `파일 형식이 (JPG, JPEG, PNG)중 하나가 아니면 예외가 발생한다`() {
            // given
            val member: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()

            // when - then
            이미지_업로드에_대한_Presigned_Url을_응답받는다(
                fileName = "cat.pdf",
                accessToken = member.token.accessToken,
            ).statusCode(BAD_REQUEST.value())
                .body("errorCode", `is`(VALIDATION_ERROR.errorCode))
                .body("message", `is`("이미지 파일[JPG, JPEG, PNG]을 업로드해주세요."))
        }

        @Test
        fun `이미지 업로드에 대한 PresignedUrl을 응답받는다`() {
            // given
            val member: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()

            // when - then
            이미지_업로드에_대한_Presigned_Url을_응답받는다(
                fileName = "cat.png",
                accessToken = member.token.accessToken,
            ).statusCode(OK.value())
                .body("preSignedUrl", startsWith(BUCKET_URL_PREFIX + BlackboxLogicControlConfig.BUCKET_UPLOAD_PREFIX + "cat.png"))
                .body("uploadFileUrl", `is`(BUCKET_URL_PREFIX + BlackboxLogicControlConfig.BUCKET_UPLOAD_PREFIX + "cat.png"))
        }
    }

    @Nested
    @DisplayName("PDF 파일 업로드에 대한 PresignedUrl 응답 API")
    internal inner class GetPdfPresignedUrl {
        @Test
        fun `파일 형식이 PDF가 아니면 예외가 발생한다`() {
            // given
            val member: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()

            // when - then
            PDF_파일_업로드에_대한_Presigned_Url을_응답받는다(
                fileName = "cat.png",
                accessToken = member.token.accessToken,
            ).statusCode(BAD_REQUEST.value())
                .body("errorCode", `is`(VALIDATION_ERROR.errorCode))
                .body("message", `is`("PDF 파일을 업로드해주세요."))
        }

        @Test
        fun `PDF 파일 업로드에 대한 PresignedUrl을 응답받는다`() {
            // given
            val member: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()

            // when - then
            PDF_파일_업로드에_대한_Presigned_Url을_응답받는다(
                fileName = "cat.pdf",
                accessToken = member.token.accessToken,
            ).statusCode(OK.value())
                .body("preSignedUrl", startsWith(BUCKET_URL_PREFIX + BlackboxLogicControlConfig.BUCKET_UPLOAD_PREFIX + "cat.pdf"))
                .body("uploadFileUrl", `is`(BUCKET_URL_PREFIX + BlackboxLogicControlConfig.BUCKET_UPLOAD_PREFIX + "cat.pdf"))
        }
    }
}
