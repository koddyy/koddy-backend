package com.koddy.server.file.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.file.application.adapter.FileManager;
import com.koddy.server.file.domain.model.PresignedUrlDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;
import java.util.Map;

import static com.koddy.server.common.utils.FileMockingUtils.createFile;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.file;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.query;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocs;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static com.koddy.server.global.exception.GlobalExceptionCode.VALIDATION_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileManagementApiController.class)
@DisplayName("File -> FileManagementApiController 테스트")
class FileManagementApiControllerTest extends ControllerTest {
    @MockBean
    private FileManager fileManager;

    @Nested
    @DisplayName("파일 업로드 API -> [POST /api/files]")
    class Upload {
        private static final String BASE_URL = "/api/files";

        @Test
        @DisplayName("파일을 업로드한다")
        void success() throws Exception {
            // given
            given(fileManager.upload(any())).willReturn("https://file-upload-url");

            // when
            final RequestBuilder requestBuilder = multipart(BASE_URL, List.of(createFile("cat.png", "image/png")));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocs("FileApi/Upload", createHttpSpecSnippets(
                            requestParts(
                                    file("file", "이미지", "파일 확장자 = JPG, JPEG, PNG", true)
                            ),
                            responseFields(
                                    body("result", "파일 업로드 URL")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("이미지 업로드에 대한 Presigned Url 응답 API -> [POST /api/files/presigned/image]")
    class GetImagePresignedUrl {
        private static final String BASE_URL = "/api/files/presigned/image";

        @Test
        @DisplayName("이미지 파일이 아니면 현재 API를 통해서 Presigned Url을 얻을 수 없다")
        void throwExceptionByNotImage() throws Exception {
            // given
            final String fileName = "cat.pdf";

            // when
            final RequestBuilder requestBuilder = get(BASE_URL, Map.of("fileName", fileName));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(VALIDATION_ERROR, "이미지 파일[JPG, JPEG, PNG]을 업로드해주세요."))
                    .andDo(failureDocs("FileApi/GetPresignedUrl/Image/Failure", createHttpSpecSnippets(
                            queryParameters(
                                    query("fileName", "프로필 이미지 사진 파일명", "프로필 이미지 사진 확장자 = JPG, JPEG, PNG", true)
                            )
                    )));
        }

        @Test
        @DisplayName("Presigned Url을 얻는다")
        void success() throws Exception {
            // given
            final String fileName = "cat.png";
            given(fileManager.createPresignedUrl(any())).willReturn(new PresignedUrlDetails(
                    "https://storage-url/path/fileName.png?X-xxx=xxx",
                    "https://storage-url/path/fileName.png"
            ));

            // when
            final RequestBuilder requestBuilder = get(BASE_URL, Map.of("fileName", fileName));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocs("FileApi/GetPresignedUrl/Image/Success", createHttpSpecSnippets(
                            queryParameters(
                                    query("fileName", "프로필 이미지 사진 파일명", "프로필 이미지 사진 확장자 = JPG, JPEG, PNG", true)
                            ),
                            responseFields(
                                    body("preSignedUrl", "Presigned Url", "PUT 요청으로 이미지 업로드 (URL + File)"),
                                    body("uploadFileUrl", "스토리지 저장 URL", "스토리지 이미지 업로드 후 서버로 전송할 URL")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("PDF 파일 업로드에 대한 Presigned Url 응답 API -> [POST /api/files/presigned/pdf]")
    class GetPdfPresignedUrl {
        private static final String BASE_URL = "/api/files/presigned/pdf";

        @Test
        @DisplayName("PDF 파일이 아니면 현재 API를 통해서 Presigned Url을 얻을 수 없다")
        void throwExceptionByNotImage() throws Exception {
            // given
            final String fileName = "cat.png";

            // when
            final RequestBuilder requestBuilder = get(BASE_URL, Map.of("fileName", fileName));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(VALIDATION_ERROR, "PDF 파일을 업로드해주세요."))
                    .andDo(failureDocs("FileApi/GetPresignedUrl/Pdf/Failure", createHttpSpecSnippets(
                            queryParameters(
                                    query("fileName", "멘토 증명자료 파일명", "멘토 증명자료 파일 확장자 = PDF", true)
                            )
                    )));
        }

        @Test
        @DisplayName("Presigned Url을 얻는다")
        void success() throws Exception {
            // given
            final String fileName = "cat.pdf";
            given(fileManager.createPresignedUrl(any())).willReturn(new PresignedUrlDetails(
                    "https://storage-url/path/fileName.pdf?X-xxx=xxx",
                    "https://storage-url/path/fileName.pdf"
            ));

            // when
            final RequestBuilder requestBuilder = get(BASE_URL, Map.of("fileName", fileName));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocs("FileApi/GetPresignedUrl/Pdf/Success", createHttpSpecSnippets(
                            queryParameters(
                                    query("fileName", "멘토 증명자료 파일명", "멘토 증명자료 파일 확장자 = PDF", true)
                            ),
                            responseFields(
                                    body("preSignedUrl", "Presigned Url", "PUT 요청으로 이미지 업로드 (URL + File)"),
                                    body("uploadFileUrl", "스토리지 저장 URL", "스토리지 이미지 업로드 후 서버로 전송할 URL")
                            )
                    )));
        }
    }
}
