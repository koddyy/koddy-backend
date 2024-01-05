package com.koddy.server.auth.presentation;

import com.koddy.server.auth.application.usecase.GetOAuthLinkUseCase;
import com.koddy.server.auth.application.usecase.LogoutUseCase;
import com.koddy.server.auth.application.usecase.OAuthLoginUseCase;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.auth.exception.OAuthUserNotFoundException;
import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.koddy.server.auth.presentation.dto.request.OAuthLoginRequest;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Map;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_OAUTH_PROVIDER;
import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.OAuthUtils.AUTHORIZATION_CODE;
import static com.koddy.server.common.utils.OAuthUtils.GOOGLE_PROVIDER;
import static com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI;
import static com.koddy.server.common.utils.OAuthUtils.STATE;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.cookie;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.header;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.path;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.query;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocs;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthApiController.class)
@DisplayName("Auth -> OAuthApiController 테스트")
class OAuthApiControllerTest extends ControllerTest {
    @MockBean
    private GetOAuthLinkUseCase getOAuthLinkUseCase;

    @MockBean
    private OAuthLoginUseCase oAuthLoginUseCase;

    @MockBean
    private LogoutUseCase logoutUseCase;

    @Nested
    @DisplayName("OAuth Authorization Code 요청을 위한 URI 조회 API [GET /api/oauth/access/{provider}]")
    class GetAuthorizationCodeForAccessGoogle {
        private static final String BASE_URL = "/api/oauth/access/{provider}";

        @Test
        @DisplayName("제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다")
        void throwExceptionByInvalidOAuthProvider() throws Exception {
            // given
            doThrow(new AuthException(INVALID_OAUTH_PROVIDER))
                    .when(getOAuthLinkUseCase)
                    .invoke(any());

            // when
            final RequestBuilder requestBuilder = get(new PathWithVariables(BASE_URL, GOOGLE_PROVIDER), Map.of("redirectUri", REDIRECT_URI));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(AuthExceptionCode.INVALID_OAUTH_PROVIDER))
                    .andDo(failureDocs("OAuthApi/Access/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth Provider", "google / kakao / zoom", true)
                            ),
                            queryParameters(
                                    query("redirectUri", "Authorization Code와 함께 redirect될 URI", true)
                            )
                    )));
        }

        @Test
        @DisplayName("Google OAuth Authorization Code 요청을 위한 URI를 생성한다")
        void success() throws Exception {
            // given
            given(getOAuthLinkUseCase.invoke(any())).willReturn("https://url-for-authorization-code");

            // when
            final RequestBuilder requestBuilder = get(new PathWithVariables(BASE_URL, GOOGLE_PROVIDER), Map.of("redirectUri", REDIRECT_URI));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").exists(),
                            jsonPath("$.result").value("https://url-for-authorization-code")
                    )
                    .andDo(successDocs("OAuthApi/Access/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth Provider", "google / kakao / zoom", true)
                            ),
                            queryParameters(
                                    query("redirectUri", "Authorization Code와 함께 redirect될 URI", true)
                            ),
                            responseFields(
                                    body("result", "Authorization Code 요청을 위한 URI")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("OAuth 로그인 API [POST /api/oauth/login/{provider}]")
    class OAuthLogin {
        private static final String BASE_URL = "/api/oauth/login/{provider}";
        private final OAuthLoginRequest request = new OAuthLoginRequest(AUTHORIZATION_CODE, REDIRECT_URI, STATE);

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() throws Exception {
            // given
            final GoogleUserResponse googleUserResponse = MENTOR_1.toGoogleUserResponse();
            doThrow(new OAuthUserNotFoundException(googleUserResponse))
                    .when(oAuthLoginUseCase)
                    .invoke(any());

            // when
            final RequestBuilder requestBuilder = post(new PathWithVariables(BASE_URL, GOOGLE_PROVIDER), request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.name").exists(),
                            jsonPath("$.name").value(googleUserResponse.name()),
                            jsonPath("$.email").exists(),
                            jsonPath("$.email").value(googleUserResponse.email()),
                            jsonPath("$.profileImageUrl").exists(),
                            jsonPath("$.profileImageUrl").value(googleUserResponse.profileImageUrl())
                    )
                    .andDo(successDocs("OAuthApi/Login/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth Provider", "google / kakao", true)
                            ),
                            requestFields(
                                    body("authorizationCode", "Authorization Code", "Authorization Code Redirect 응답 시 QueryParam으로 넘어오는 Code 값", true),
                                    body("redirectUri", "Redirect Uri", "Authorization Code 요청 시 redirectUri와 반드시 동일한 값", true),
                                    body("state", "State 값", "Authorization Code Redirect 응답 시 QueryParam으로 넘어오는 State 값", true)
                            ),
                            responseFields(
                                    body("name", "회원가입 진행 시 이름 정보 기본값"),
                                    body("email", "회원가입 진행 시 이메일 정보 [Read-Only]"),
                                    body("profileImageUrl", "회원가입 진행 시 프로필 이미지 정보 기본값")
                            )
                    )));
        }

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() throws Exception {
            // given
            final AuthMember loginResponse = MENTOR_1.toAuthMember();
            given(oAuthLoginUseCase.invoke(any())).willReturn(loginResponse);

            // when
            final RequestBuilder requestBuilder = post(new PathWithVariables(BASE_URL, GOOGLE_PROVIDER), request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").exists(),
                            jsonPath("$.id").value(loginResponse.member().id()),
                            jsonPath("$.name").exists(),
                            jsonPath("$.name").value(loginResponse.member().name()),
                            jsonPath("$.profileImageUrl").exists(),
                            jsonPath("$.profileImageUrl").value(loginResponse.member().profileImageUrl())
                    )
                    .andDo(successDocs("OAuthApi/Login/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth Provider", "google / kakao", true)
                            ),
                            requestFields(
                                    body("authorizationCode", "Authorization Code", "Authorization Code Redirect 응답 시 QueryParam으로 넘어오는 Code 값", true),
                                    body("redirectUri", "Redirect Uri", "Authorization Code 요청 시 redirectUri와 반드시 동일한 값", true),
                                    body("state", "State 값", "Authorization Code Redirect 응답 시 QueryParam으로 넘어오는 State 값", true)
                            ),
                            responseHeaders(
                                    header(AUTHORIZATION, "Access Token")
                            ),
                            responseCookies(
                                    cookie(COOKIE_REFRESH_TOKEN, "Refresh Token")
                            ),
                            responseFields(
                                    body("id", "사용자 ID(PK)"),
                                    body("name", "사용자 이름"),
                                    body("profileImageUrl", "사용자 프로필 이미지")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("로그아웃 API [POST /api/oauth/logout] - Required AccessToken")
    class Logout {
        private static final String BASE_URL = "/api/oauth/logout";
        private final Member<?> member = MENTOR_1.toDomain().apply(1L);

        @Test
        @DisplayName("로그아웃을 진행한다")
        void success() throws Exception {
            // given
            mockingToken(true, member.getId(), member.getRoleTypes());
            doNothing()
                    .when(logoutUseCase)
                    .invoke(any());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("OAuthApi/Logout"));
        }
    }
}
