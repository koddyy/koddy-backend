package com.koddy.server.auth.presentation

import com.koddy.server.auth.application.usecase.GetOAuthLinkUseCase
import com.koddy.server.auth.application.usecase.LogoutUseCase
import com.koddy.server.auth.application.usecase.OAuthLoginUseCase
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.auth.exception.OAuthUserNotFoundException
import com.koddy.server.auth.presentation.request.OAuthLoginRequest
import com.koddy.server.auth.presentation.response.LoginResponse
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.utils.OAuthDummy.AUTHORIZATION_CODE
import com.koddy.server.common.utils.OAuthDummy.GOOGLE_PROVIDER
import com.koddy.server.common.utils.OAuthDummy.REDIRECT_URI
import com.koddy.server.common.utils.OAuthDummy.STATE
import com.koddy.server.common.utils.TokenDummy
import com.koddy.server.common.utils.TokenDummy.ACCESS_TOKEN
import com.koddy.server.global.ResponseWrapper
import com.koddy.server.global.exception.OAuthExceptionResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders

@WebMvcTest(OAuthApi::class)
@DisplayName("Auth -> OAuthApi 테스트")
internal class OAuthApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var getOAuthLinkUseCase: GetOAuthLinkUseCase

    @MockkBean
    private lateinit var oAuthLoginUseCase: OAuthLoginUseCase

    @MockkBean
    private lateinit var logoutUseCase: LogoutUseCase

    @Nested
    @DisplayName("OAuth Authorization Code 요청을 위한 URI 조회 API [GET /api/oauth/access/{provider}]")
    internal inner class GetAuthorizationCodeForAccessGoogle {
        private val baseUrl = "/api/oauth/access/{provider}"

        @Test
        fun `제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다`() {
            val exceptionCode = AuthExceptionCode.INVALID_OAUTH_PROVIDER
            every { getOAuthLinkUseCase.invoke(any()) } throws AuthException(exceptionCode)

            getRequest(baseUrl, arrayOf(GOOGLE_PROVIDER)) {
                param("redirectUri", REDIRECT_URI)
            }.andExpect {
                status { isBadRequest() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocs("OAuthApi/Access/Failure") {
                    pathParameters(
                        "provider" type STRING means "OAuth Provider" constraint "google kakao zoom",
                    )
                    queryParameters(
                        "redirectUri" type STRING means "Authorization Code와 함께 redirect될 URI",
                    )
                }
            }
        }

        @Test
        fun `Google OAuth Authorization Code 요청을 위한 URI를 생성한다`() {
            val response = "https://url-for-authorization-code"
            every { getOAuthLinkUseCase.invoke(any()) } returns response

            getRequest(baseUrl, arrayOf(GOOGLE_PROVIDER)) {
                param("redirectUri", REDIRECT_URI)
            }.andExpect {
                status { isOk() }
                content { success(ResponseWrapper(response)) }
            }.andDo {
                makeSuccessDocs("OAuthApi/Access/Success") {
                    pathParameters(
                        "provider" type STRING means "OAuth Provider" constraint "google kakao zoom",
                    )
                    queryParameters(
                        "redirectUri" type STRING means "Authorization Code와 함께 redirect될 URI",
                    )
                    responseFields(
                        "result" type STRING means "Authorization Code 요청을 위한 URI",
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("OAuth 로그인 API [POST /api/oauth/login/{provider}]")
    internal inner class OAuthLogin {
        private val baseUrl = "/api/oauth/login/{provider}"
        private val request = OAuthLoginRequest(AUTHORIZATION_CODE, REDIRECT_URI, STATE)

        @Test
        fun `Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 회원가입을 진행한다`() {
            val response = mentorFixture(sequence = 1).toGoogleUserResponse()
            every { oAuthLoginUseCase.invoke(any()) } throws OAuthUserNotFoundException(response)

            postRequest(baseUrl, arrayOf(GOOGLE_PROVIDER)) {
                bodyContent(request)
            }.andExpect {
                status { isNotFound() }
                content { exception(OAuthExceptionResponse(response)) }
            }.andDo {
                makeSuccessDocs("OAuthApi/Login/Failure") {
                    pathParameters(
                        "provider" type STRING means "OAuth Provider" constraint "google kakao",
                    )
                    requestFields(
                        "authorizationCode" type STRING means "Authorization Code" constraint "QueryParam -> code",
                        "redirectUri" type STRING means "Authorization Code와 함께 redirect될 URI",
                        "state" type STRING means "State 값" constraint "QueryParam -> state",
                    )
                    responseFields(
                        "id" type STRING means "소셜 플랫폼 고유 ID" constraint "ReadOnly",
                        "name" type STRING means "소셜 플랫폼 이름",
                        "email" type STRING means "소셜 플랫폼 이메일" constraint "ReadOnly",
                        "profileImageUrl" type STRING means "소셜 플랫폼 프로필 이미지 URL",
                    )
                }
            }
        }

        @Test
        fun `Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다`() {
            val response = AuthMember(
                id = 1L,
                name = "이름",
                token = TokenDummy.basicAuthToken(),
            )
            every { oAuthLoginUseCase.invoke(any()) } returns response

            postRequest(baseUrl, arrayOf(GOOGLE_PROVIDER)) {
                bodyContent(request)
            }.andExpect {
                status { isOk() }
                content { success(LoginResponse(id = response.id, name = response.name)) }
            }.andDo {
                makeSuccessDocs("OAuthApi/Access/Success") {
                    pathParameters(
                        "provider" type STRING means "OAuth Provider" constraint "google kakao zoom",
                    )
                    requestFields(
                        "authorizationCode" type STRING means "Authorization Code" constraint "QueryParam -> code",
                        "redirectUri" type STRING means "Authorization Code와 함께 redirect될 URI",
                        "state" type STRING means "State 값" constraint "QueryParam -> state",
                    )
                    responseHeaders(
                        AuthToken.ACCESS_TOKEN_HEADER type STRING means "Access Token",
                        HttpHeaders.SET_COOKIE type STRING means "Set Refresh Token",
                    )
                    responseCookies(
                        AuthToken.REFRESH_TOKEN_HEADER type STRING means "Refresh Token",
                    )
                    responseFields(
                        "id" type NUMBER means "사용자 ID(PK)",
                        "name" type STRING means "사용자 이름",
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("로그아웃 API [POST /api/oauth/logout]")
    internal inner class Logout {
        private val baseUrl = "/api/oauth/logout"

        @Test
        fun `로그아웃을 진행한다`() {
            justRun { logoutUseCase.invoke(any()) }

            postRequest(baseUrl) {
                accessToken(ACCESS_TOKEN)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("OAuthApi/Logout") {}
            }
        }
    }
}
