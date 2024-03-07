package com.koddy.server.acceptance.file

import com.koddy.server.acceptance.RequestHelper
import io.restassured.response.ValidatableResponse

object FileAcceptanceStep {
    fun 파일을_업로드한다(
        fileName: String,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.multipartRequest(
            uri = "/api/files",
            fileName = fileName,
            accessToken = accessToken,
        )
    }

    fun 이미지_업로드에_대한_Presigned_Url을_응답받는다(
        fileName: String,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.getRequestWithAccessToken(
            uri = "/api/files/presigned/image?fileName=$fileName",
            accessToken = accessToken,
        )
    }

    fun PDF_파일_업로드에_대한_Presigned_Url을_응답받는다(
        fileName: String,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.getRequestWithAccessToken(
            uri = "/api/files/presigned/pdf?fileName=$fileName",
            accessToken = accessToken,
        )
    }
}
