package com.koddy.server.auth.infrastructure.social.kakao

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse
import com.koddy.server.auth.infrastructure.social.OAuthConnector
import com.koddy.server.auth.infrastructure.social.OAuthConnector.Companion.BEARER_TOKEN_TYPE
import com.koddy.server.auth.infrastructure.social.OAuthConnector.Companion.OAUTH_CONTENT_TYPE
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoTokenResponse
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoUserResponse
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

@Component
class KakaoOAuthConnector(
    private val properties: KakaoOAuthProperties,
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
                KakaoTokenResponse::class.java,
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
        return headers
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
            add("client_id", properties.clientId)
            add("client_secret", properties.clientSecret)
        }
        return params
    }

    override fun fetchUserInfo(accessToken: String): OAuthUserResponse {
        try {
            return restTemplate.exchange(
                properties.userInfoUrl,
                GET,
                HttpEntity<Any>(createUserInfoRequestHeader(accessToken)),
                KakaoUserResponse::class.java,
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
        headers[AUTHORIZATION] = "$BEARER_TOKEN_TYPE $accessToken"
        return headers
    }
}
