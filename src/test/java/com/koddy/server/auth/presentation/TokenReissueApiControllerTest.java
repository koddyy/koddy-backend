package com.koddy.server.auth.presentation;

import com.koddy.server.auth.application.usecase.ReissueTokenUseCase;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN;
import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.cookie;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.header;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createResponseSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithRefreshToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithRefreshToken;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenReissueApiController.class)
@DisplayName("Auth -> TokenReissueApiController 테스트")
class TokenReissueApiControllerTest extends ControllerTest {
    @MockBean
    private ReissueTokenUseCase reissueTokenUseCase;

    @Nested
    @DisplayName("토큰 재발급 API [POST /api/token/reissue] - Required RefreshToken")
    class ReissueToken {
        private static final String BASE_URL = "/api/token/reissue";

        @Test
        @DisplayName("유효하지 않은 RefreshToken으로 인해 토큰 재발급에 실패한다")
        void throwExceptionByExpiredRefreshToken() throws Exception {
            // given
            mockingTokenWithInvalidException();

            // when
            final RequestBuilder requestBuilder = postWithRefreshToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_TOKEN))
                    .andDo(failureDocsWithRefreshToken("TokenReissueApi/Failure"));
        }

        @Test
        @DisplayName("사용자 소유의 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다")
        void success() throws Exception {
            // given
            final Member member = MENTOR_1.toDomain().apply(1L);
            mockingToken(true, member.getId(), member.getRoleTypes());
            given(reissueTokenUseCase.invoke(any())).willReturn(new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN));

            // when
            final RequestBuilder requestBuilder = postWithRefreshToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(status().isNoContent())
                    .andDo(successDocsWithRefreshToken("TokenReissueApi/Success", createResponseSnippets(
                            responseHeaders(
                                    header(AUTHORIZATION, "Access Token"),
                                    header(SET_COOKIE, "Set Refresh Token")
                            ),
                            responseCookies(
                                    cookie(COOKIE_REFRESH_TOKEN, "Refresh Token")
                            )
                    )));
        }
    }
}
