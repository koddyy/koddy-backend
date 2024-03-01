package com.koddy.server.auth.presentation;

import com.koddy.server.auth.application.usecase.ReissueTokenUseCase;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.common.ApiDocsTest;
import com.koddy.server.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.cookie;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.header;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithRefreshToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithRefreshToken;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth -> TokenReissueApi 테스트")
class TokenReissueApiTest extends ApiDocsTest {
    @Autowired
    private ReissueTokenUseCase reissueTokenUseCase;

    private final Member<?> member = MENTOR_1.toDomain().apply(1L);

    @Nested
    @DisplayName("토큰 재발급 API [POST /api/token/reissue]")
    class ReissueToken {
        private static final String BASE_URL = "/api/token/reissue";

        @Test
        @DisplayName("유효하지 않은 RefreshToken으로 인해 토큰 재발급에 실패한다")
        void throwExceptionByExpiredRefreshToken() {
            // given
            doThrow(new AuthException(AuthExceptionCode.INVALID_TOKEN))
                    .when(tokenProvider)
                    .validateRefreshToken(anyString());
            given(tokenProvider.getId(anyString())).willReturn(member.getId());
            given(tokenProvider.getAuthority(anyString())).willReturn(member.getAuthority());

            // when - then
            failedExecute(
                    postRequestWithRefreshToken(BASE_URL),
                    status().isUnauthorized(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_TOKEN),
                    failureDocsWithRefreshToken("TokenReissueApi/Failure")
            );
        }

        @Test
        @DisplayName("사용자 소유의 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다")
        void success() {
            // given
            given(tokenProvider.getId(anyString())).willReturn(member.getId());
            given(tokenProvider.getAuthority(anyString())).willReturn(member.getAuthority());
            given(reissueTokenUseCase.invoke(any())).willReturn(new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN));

            // when - then
            successfulExecute(
                    postRequestWithRefreshToken(BASE_URL),
                    status().isNoContent(),
                    successDocsWithRefreshToken("TokenReissueApi/Success", createHttpSpecSnippets(
                            responseHeaders(
                                    header(ACCESS_TOKEN_HEADER, "Access Token"),
                                    header(SET_COOKIE, "Set Refresh Token")
                            ),
                            responseCookies(
                                    cookie(REFRESH_TOKEN_HEADER, "Refresh Token")
                            )
                    ))
            );
        }
    }
}
