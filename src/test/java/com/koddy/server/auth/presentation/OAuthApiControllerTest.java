package com.koddy.server.auth.presentation;

import com.koddy.server.auth.application.usecase.GetOAuthLinkUseCase;
import com.koddy.server.auth.application.usecase.LogoutUseCase;
import com.koddy.server.auth.application.usecase.OAuthLoginUseCase;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.auth.exception.OAuthUserNotFoundException;
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse;
import com.koddy.server.auth.presentation.dto.request.OAuthLoginRequest;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_OAUTH_PROVIDER;
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
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth -> OAuthApiController 테스트")
class OAuthApiControllerTest extends ControllerTest {
    @Autowired
    private GetOAuthLinkUseCase getOAuthLinkUseCase;

    @Autowired
    private OAuthLoginUseCase oAuthLoginUseCase;

    @Autowired
    private LogoutUseCase logoutUseCase;

    @Nested
    @DisplayName("OAuth Authorization Code 요청을 위한 URI 조회 API [GET /api/oauth/access/{provider}]")
    class GetAuthorizationCodeForAccessGoogle {
        private static final String BASE_URL = "/api/oauth/access/{provider}";

        @Test
        @DisplayName("제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다")
        void throwExceptionByInvalidOAuthProvider() {
            // given
            doThrow(new AuthException(INVALID_OAUTH_PROVIDER))
                    .when(getOAuthLinkUseCase)
                    .invoke(any());

            // when - then
            failedExecute(
                    getRequest(new UrlWithVariables(BASE_URL, GOOGLE_PROVIDER), Map.of("redirectUri", REDIRECT_URI)),
                    status().isBadRequest(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_OAUTH_PROVIDER),
                    failureDocs("OAuthApi/Access/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth Provider", "google kakao zoom", true)
                            ),
                            queryParameters(
                                    query("redirectUri", "Authorization Code와 함께 redirect될 URI", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("Google OAuth Authorization Code 요청을 위한 URI를 생성한다")
        void success() {
            // given
            given(getOAuthLinkUseCase.invoke(any())).willReturn("https://url-for-authorization-code");

            // when - then
            successfulExecute(
                    getRequest(new UrlWithVariables(BASE_URL, GOOGLE_PROVIDER), Map.of("redirectUri", REDIRECT_URI)),
                    status().isOk(),
                    successDocs("OAuthApi/Access/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth Provider", "google kakao zoom", true)
                            ),
                            queryParameters(
                                    query("redirectUri", "Authorization Code와 함께 redirect될 URI", true)
                            ),
                            responseFields(
                                    body("result", "Authorization Code 요청을 위한 URI")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("OAuth 로그인 API [POST /api/oauth/login/{provider}]")
    class OAuthLogin {
        private static final String BASE_URL = "/api/oauth/login/{provider}";
        private final OAuthLoginRequest request = new OAuthLoginRequest(AUTHORIZATION_CODE, REDIRECT_URI, STATE);

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() {
            // given
            final GoogleUserResponse googleUserResponse = MENTOR_1.toGoogleUserResponse();
            doThrow(new OAuthUserNotFoundException(googleUserResponse))
                    .when(oAuthLoginUseCase)
                    .invoke(any());

            // when - then
            successfulExecute(
                    postRequest(new UrlWithVariables(BASE_URL, GOOGLE_PROVIDER), request),
                    status().isNotFound(),
                    successDocs("OAuthApi/Login/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth Provider", "google kakao", true)
                            ),
                            requestFields(
                                    body("authorizationCode", "Authorization Code", "QueryParam -> code", true),
                                    body("redirectUri", "Redirect Uri", "Authorization Code 요청 URI와 동일 값", true),
                                    body("state", "State 값", "QueryParam -> state", true)
                            ),
                            responseFields(
                                    body("id", "소셜 플랫폼 고유 ID", "ReadOnly"),
                                    body("name", "소셜 플랫폼 이름"),
                                    body("email", "소셜 플랫폼 이메일", "ReadOnly"),
                                    body("profileImageUrl", "프로필 이미지 URL")
                            )
                    ))
            );
        }

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() {
            // given
            final AuthMember loginResponse = MENTOR_1.toAuthMember();
            given(oAuthLoginUseCase.invoke(any())).willReturn(loginResponse);

            // when - then
            successfulExecute(
                    postRequest(new UrlWithVariables(BASE_URL, GOOGLE_PROVIDER), request),
                    status().isOk(),
                    successDocs("OAuthApi/Login/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth Provider", "google kakao", true)
                            ),
                            requestFields(
                                    body("authorizationCode", "Authorization Code", "QueryParam -> code", true),
                                    body("redirectUri", "Redirect Uri", "Authorization Code 요청 URI와 동일 값", true),
                                    body("state", "State 값", "QueryParam -> state", true)
                            ),
                            responseHeaders(
                                    header(ACCESS_TOKEN_HEADER, "Access Token")
                            ),
                            responseCookies(
                                    cookie(REFRESH_TOKEN_HEADER, "Refresh Token")
                            ),
                            responseFields(
                                    body("id", "사용자 ID(PK)"),
                                    body("name", "사용자 이름")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("로그아웃 API [POST /api/oauth/logout]")
    class Logout {
        private static final String BASE_URL = "/api/oauth/logout";
        private final Member<?> member = MENTOR_1.toDomain().apply(1L);

        @Test
        @DisplayName("로그아웃을 진행한다")
        void success() {
            // given
            applyToken(true, member);
            doNothing()
                    .when(logoutUseCase)
                    .invoke(any());

            // when - then
            successfulExecute(
                    postRequestWithAccessToken(BASE_URL),
                    status().isNoContent(),
                    successDocsWithAccessToken("OAuthApi/Logout")
            );
        }
    }
}
