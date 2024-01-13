package com.koddy.server.acceptance.file;

import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.koddy.server.acceptance.CommonRequestFixture.getRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.multipartRequest;

public class FileAcceptanceStep {
    public static ValidatableResponse 파일을_업로드한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/files")
                .build()
                .toUri()
                .getPath();

        return multipartRequest("cat.png", accessToken, uri);
    }

    public static ValidatableResponse 이미지_업로드에_대한_Presigned_Url을_응답받는다(
            final String fileName,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/files/presigned/image?fileName={fileName}")
                .build(fileName)
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse PDF_파일_업로드에_대한_Presigned_Url을_응답받는다(
            final String fileName,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/files/presigned/pdf?fileName={fileName}")
                .build(fileName)
                .getPath();

        return getRequest(accessToken, uri);
    }
}