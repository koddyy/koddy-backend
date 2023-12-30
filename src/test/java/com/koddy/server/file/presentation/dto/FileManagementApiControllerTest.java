package com.koddy.server.file.presentation.dto;

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
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
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
    @DisplayName("Presigned Url 응답 API -> [POST /api/files/presigned]")
    class GetPresignedUrl {
        private static final String BASE_URL = "/api/files/presigned";

        @Test
        @DisplayName("Presigned Url을 얻는다")
        void success() throws Exception {
            // given
            given(fileManager.createPresignedUrl(any())).willReturn(new PresignedUrlDetails(
                    "https://storage-url/path/fileName.png?X-xxx=xxx",
                    "https://storage-url/path/fileName.png"
            ));

            // when
            final RequestBuilder requestBuilder = get(BASE_URL, Map.of("fileName", "cat.png"));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocs("FileApi/GetPresignedUrl", createHttpSpecSnippets(
                            queryParameters(
                                    query("fileName", "파일명", "파일 확장자 = JPG, JPEG, PNG", true)
                            ),
                            responseFields(
                                    body("preSignedUrl", "Presigned Url", "PUT 요청으로 이미지 업로드 (URL + File)"),
                                    body("uploadFileUrl", "스토리지 저장 URL", "스토리지 이미지 업로드 후 서버로 전송할 URL")
                            )
                    )));
        }
    }
}
