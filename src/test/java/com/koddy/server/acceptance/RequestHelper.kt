package com.koddy.server.acceptance

import com.koddy.server.auth.domain.model.AuthToken
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.http.Cookie
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.IOException
import java.util.function.Function

private fun RequestSpecification.When(): RequestSpecification {
    return this.`when`()
}

object RequestHelper {
    @JvmStatic
    fun getRequest(uri: String): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .When()[uri]
        }

    @JvmStatic
    fun getRequestWithAccessToken(
        uri: String,
        accessToken: String,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .When()[uri]
        }

    @JvmStatic
    fun postRequest(uri: String): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .When()
                .post(uri)
        }

    @JvmStatic
    fun postRequest(
        url: String,
        body: Any,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .body(body)
                .When()
                .post(url)
        }

    @JvmStatic
    fun postRequestWithAccessToken(
        uri: String,
        accessToken: String,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .When()
                .post(uri)
        }

    @JvmStatic
    fun postRequestWithRefreshToken(
        uri: String,
        refreshToken: String,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .cookie(Cookie.Builder(AuthToken.REFRESH_TOKEN_HEADER, refreshToken).build())
                .When()
                .post(uri)
        }

    @JvmStatic
    fun postRequestWithAccessToken(
        uri: String,
        body: Any,
        accessToken: String,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .body(body)
                .When()
                .post(uri)
        }

    @JvmStatic
    fun multipartRequest(
        url: String,
        fileName: String,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.MULTIPART)
                .multiPart("file", getFile(fileName))
                .When()
                .post(url)
        }

    fun multipartRequest(
        uri: String,
        params: Map<String, String>,
    ): ValidatableResponse {
        val request = RestAssured.given().log().all()
            .contentType(ContentType.MULTIPART)
            .request()
        params.keys.forEach { request.multiPart(it, params[it]) }

        return request.post(uri)
            .then().log().all()
    }

    fun multipartRequest(
        uri: String,
        fileName: String,
        params: Map<String, String>,
    ): ValidatableResponse {
        val request = RestAssured.given().log().all()
            .contentType(ContentType.MULTIPART)
            .multiPart("file", getFile(fileName))
            .request()
        params.keys.forEach { request.multiPart(it, params[it]) }

        return request.post(uri)
            .then().log().all()
    }

    @JvmStatic
    fun multipartRequest(
        uri: String,
        fileName: String,
        accessToken: String,
    ): ValidatableResponse {
        return request {
            it
                .contentType(ContentType.MULTIPART)
                .auth().oauth2(accessToken)
                .multiPart("file", getFile(fileName))
                .When()
                .post(uri)
        }
    }

    fun multipartRequest(
        uri: String,
        params: Map<String, String>,
        accessToken: String,
    ): ValidatableResponse {
        val request = RestAssured.given().log().all()
            .contentType(ContentType.MULTIPART)
            .auth().oauth2(accessToken)
            .request()
        params.keys.forEach { request.multiPart(it, params[it]) }

        return request.post(uri)
            .then().log().all()
    }

    fun multipartRequest(
        uri: String,
        fileName: String,
        params: Map<String, String>,
        accessToken: String,
    ): ValidatableResponse {
        val request = RestAssured.given().log().all()
            .contentType(ContentType.MULTIPART)
            .auth().oauth2(accessToken)
            .multiPart("file", getFile(fileName))
            .request()
        params.keys.forEach { request.multiPart(it, params[it]) }

        return request.post(uri)
            .then().log().all()
    }

    fun multipartRequest(
        uri: String,
        fileNames: List<String>,
    ): ValidatableResponse {
        val request = RestAssured.given().log().all()
            .request()
        fileNames.forEach { request.multiPart("files", getFile(it)) }

        return request.post(uri)
            .then().log().all()
    }

    fun multipartRequest(
        uri: String,
        fileNames: List<String>,
        params: Map<String, String>,
    ): ValidatableResponse {
        val request = RestAssured.given().log().all()
            .contentType(ContentType.MULTIPART)
            .request()
        fileNames.forEach { request.multiPart("files", getFile(it)) }
        params.keys.forEach { request.multiPart(it, params[it]) }

        return request.post(uri)
            .then().log().all()
    }

    fun multipartRequest(
        uri: String,
        fileNames: List<String>,
        accessToken: String,
    ): ValidatableResponse {
        val request = RestAssured.given().log().all()
            .contentType(ContentType.MULTIPART)
            .auth().oauth2(accessToken)
            .request()
        fileNames.forEach { request.multiPart("files", getFile(it)) }

        return request.post(uri)
            .then().log().all()
    }

    fun multipartRequest(
        uri: String,
        fileNames: List<String>,
        params: Map<String, String>,
        accessToken: String,
    ): ValidatableResponse {
        val request = RestAssured.given().log().all()
            .contentType(ContentType.MULTIPART)
            .auth().oauth2(accessToken)
            .request()
        fileNames.forEach { request.multiPart("files", getFile(it)) }
        params.keys.forEach { request.multiPart(it, params[it]) }

        return request.post(uri)
            .then().log().all()
    }

    fun patchRequest(uri: String): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .When()
                .patch(uri)
        }

    @JvmStatic
    fun patchRequestWithAccessToken(
        uri: String,
        accessToken: String,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .When()
                .patch(uri)
        }

    @JvmStatic
    fun patchRequestWithAccessToken(
        uri: String,
        body: Any,
        accessToken: String,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .body(body)
                .When()
                .patch(uri)
        }

    fun deleteRequest(uri: String): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .When()
                .delete(uri)
        }

    @JvmStatic
    fun deleteRequestWithAccessToken(
        uri: String,
        accessToken: String,
    ): ValidatableResponse =
        request {
            it.contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .When()
                .delete(uri)
        }

    private fun request(function: Function<RequestSpecification, Response>): ValidatableResponse {
        val request: RequestSpecification = RestAssured.given().log().all()
        val response: Response = function.apply(request)
        return response.then().log().all()
    }

    private fun getFile(fileName: String): File {
        try {
            return ClassPathResource(FILE_BASE_PATH + fileName).file
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private const val FILE_BASE_PATH = "files/"
}
