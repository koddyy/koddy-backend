package com.koddy.server.file.presentation

import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.utils.FileVirtualCreator
import com.koddy.server.file.application.usecase.RegisterPresignedUrlUseCase
import com.koddy.server.file.application.usecase.UploadFileUseCase
import com.koddy.server.file.domain.model.PresignedUrlDetails
import com.koddy.server.global.ResponseWrapper
import com.koddy.server.global.exception.GlobalExceptionCode
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(FileManagementApi::class)
@DisplayName("File -> FileManagementApi 테스트")
internal class FileManagementApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var uploadFileUseCase: UploadFileUseCase

    @MockkBean
    private lateinit var registerPresignedUrlUseCase: RegisterPresignedUrlUseCase

    @Nested
    @DisplayName("파일 업로드 API -> [POST /api/files]")
    internal inner class Upload {
        private val baseUrl = "/api/files"

        private val fileForms: Array<DocumentField> = arrayOf(
            "file" type STRING means "업로드 파일" constraint "파일 확장자 = JPG, JPEG, PNG, PDF",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "result" type STRING means "업로드된 파일 URL",
        )

        @Test
        fun `파일을 업로드한다`() {
            val response = "https://file-upload-url"
            every { uploadFileUseCase.invoke(any()) } returns response

            multipartRequest(
                baseUrl,
                listOf(FileVirtualCreator.createFile("cat.png", "image/png")),
            ) {
                accessToken(common)
            }.andExpect {
                status { isOk() }
                content { success(ResponseWrapper(response)) }
            }.andDo {
                makeSuccessDocsWithAccessToken("FileApi/Upload") {
                    fileForms(*fileForms)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("이미지 업로드에 대한 Presigned Url 응답 API -> [POST /api/files/presigned/image]")
    internal inner class GetImagePresignedUrl {
        private val baseUrl = "/api/files/presigned/image"

        private val queryParameters: Array<DocumentField> = arrayOf(
            "fileName" type STRING means "프로필 이미지 사진 파일명" constraint "파일 확장자 = JPG, JPEG, PNG",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "preSignedUrl" type STRING means "Presigned Url" constraint "PUT 요청으로 이미지 업로드 (URL + File)",
            "uploadFileUrl" type STRING means "스토리지 저장 URL" constraint "스토리지 이미지 업로드 후 서버로 전송할 URL",
        )

        @Test
        fun `이미지 파일이 아니면 Presigned Url을 얻을 수 없다`() {
            val exceptionCode = GlobalExceptionCode.VALIDATION_ERROR
            val exceptionMessage = "이미지 파일[JPG, JPEG, PNG]을 업로드해주세요."

            getRequest(baseUrl) {
                accessToken(common)
                param("fileName", "cat.pdf")
            }.andExpect {
                status { isBadRequest() }
                content { exception(exceptionCode, exceptionMessage) }
            }.andDo {
                makeFailureDocsWithAccessToken("FileApi/GetPresignedUrl/Image/Failure") {
                    queryParameters(*queryParameters)
                }
            }
        }

        @Test
        fun `이미지 파일에 대한 Presigned Url을 얻는다`() {
            val response = PresignedUrlDetails(
                preSignedUrl = "https://storage-url/path/fileName.png?X-xxx=xxx",
                uploadFileUrl = "https://storage-url/path/fileName.png",
            )
            every { registerPresignedUrlUseCase.invoke(any()) } returns response

            getRequest(baseUrl) {
                accessToken(common)
                param("fileName", "cat.png")
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("FileApi/GetPresignedUrl/Image/Success") {
                    queryParameters(*queryParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("PDF 파일 업로드에 대한 Presigned Url 응답 API -> [POST /api/files/presigned/pdf]")
    internal inner class GetPdfPresignedUrl {
        private val baseUrl = "/api/files/presigned/pdf"

        private val queryParameters: Array<DocumentField> = arrayOf(
            "fileName" type STRING means "멘토 증명자료 파일명" constraint "파일 확장자 = PDF",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "preSignedUrl" type STRING means "Presigned Url" constraint "PUT 요청으로 이미지 업로드 (URL + File)",
            "uploadFileUrl" type STRING means "스토리지 저장 URL" constraint "스토리지 이미지 업로드 후 서버로 전송할 URL",
        )

        @Test
        fun `PDF 파일이 아니면 Presigned Url을 얻을 수 없다`() {
            val exceptionCode = GlobalExceptionCode.VALIDATION_ERROR
            val exceptionMessage = "PDF 파일을 업로드해주세요."

            getRequest(baseUrl) {
                accessToken(common)
                param("fileName", "cat.png")
            }.andExpect {
                status { isBadRequest() }
                content { exception(exceptionCode, exceptionMessage) }
            }.andDo {
                makeFailureDocsWithAccessToken("FileApi/GetPresignedUrl/Pdf/Failure") {
                    queryParameters(*queryParameters)
                }
            }
        }

        @Test
        fun `이미지 파일에 대한 Presigned Url을 얻는다`() {
            val response = PresignedUrlDetails(
                preSignedUrl = "https://storage-url/path/fileName.pdf?X-xxx=xxx",
                uploadFileUrl = "https://storage-url/path/fileName.pdf",
            )
            every { registerPresignedUrlUseCase.invoke(any()) } returns response

            getRequest(baseUrl) {
                accessToken(common)
                param("fileName", "cat.pdf")
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("FileApi/GetPresignedUrl/Pdf/Success") {
                    queryParameters(*queryParameters)
                    responseFields(*responseFields)
                }
            }
        }
    }
}
