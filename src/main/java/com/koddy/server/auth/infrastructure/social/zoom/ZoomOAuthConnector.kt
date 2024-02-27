package com.koddy.server.auth.infrastructure.social.zoom

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse
import com.koddy.server.auth.infrastructure.social.OAuthConnector
import com.koddy.server.auth.infrastructure.social.OAuthConnector.Companion.OAUTH_CONTENT_TYPE
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomTokenResponse
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse
import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR
import com.koddy.server.global.log.logger
import org.slf4j.Logger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets
import java.util.Base64

@Component
class ZoomOAuthConnector(
    private val properties: ZoomOAuthProperties,
    private val restTemplate: RestTemplate,
) : OAuthConnector {
    private val log: Logger = logger()

    override fun fetchToken(
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthTokenResponse {
        try {
            return restTemplate.exchange(
                properties.tokenUrl,
                POST,
                HttpEntity(createTokenRequestParams(code, redirectUri, state), createTokenRequestHeader()),
                ZoomTokenResponse::class.java,
            ).body!!
        } catch (ex: RestClientException) {
            log.error("OAuth Error... ", ex)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        } catch (ex: Exception) {
            log.error("Undefined Error... ", ex)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        }
    }

    private fun createTokenRequestHeader(): HttpHeaders {
        val headers = HttpHeaders()
        headers[CONTENT_TYPE] = OAUTH_CONTENT_TYPE
        headers[AUTHORIZATION] = "Basic ${createEncodedAuthorizationHeaderWithClientProperties()}"
        return headers
    }

    private fun createEncodedAuthorizationHeaderWithClientProperties(): String {
        val value = "${properties.clientId}:${properties.clientSecret}"
        val encode: ByteArray = Base64.getEncoder().encode(value.toByteArray())
        return String(encode, StandardCharsets.UTF_8)
    }

    private fun createTokenRequestParams(
        code: String,
        redirectUri: String,
        state: String,
    ): MultiValueMap<String, String> {
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        with(params) {
            add("grant_type", properties.grantType)
            add("code", code)
            add("redirect_uri", redirectUri)
            add("state", state)
        }
        return params
    }

    override fun fetchUserInfo(accessToken: String): OAuthUserResponse {
        try {
            return restTemplate.exchange(
                properties.userInfoUrl,
                GET,
                HttpEntity<Any>(createUserInfoRequestHeader(accessToken)),
                ZoomUserResponse::class.java,
            ).body!!
        } catch (ex: RestClientException) {
            log.error("OAuth Error... ", ex)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        } catch (ex: Exception) {
            log.error("Undefined Error... ", ex)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        }
    }

    private fun createUserInfoRequestHeader(accessToken: String): HttpHeaders {
        val headers = HttpHeaders()
        headers[CONTENT_TYPE] = OAUTH_CONTENT_TYPE
        headers[AUTHORIZATION] = "${OAuthConnector.BEARER_TOKEN_TYPE} $accessToken"
        return headers
    }
}
