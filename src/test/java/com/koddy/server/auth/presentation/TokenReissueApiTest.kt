package com.koddy.server.auth.presentation

import com.koddy.server.auth.application.usecase.ReissueTokenUseCase
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.utils.TokenDummy.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenDummy.INVALID_REFRESH_TOKEN
import com.koddy.server.common.utils.TokenDummy.REFRESH_TOKEN
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders

@WebMvcTest(TokenReissueApi::class)
@DisplayName("Auth -> TokenReissueApi 테스트")
internal class TokenReissueApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var reissueTokenUseCase: ReissueTokenUseCase

    @Nested
    @DisplayName("토큰 재발급 API [POST /api/token/reissue]")
    internal inner class ReissueToken {
        private val baseUrl = "/api/token/reissue"

        private val responseHeaders: Array<DocumentField> = arrayOf(
            AuthToken.ACCESS_TOKEN_HEADER type STRING means "Access Token",
            HttpHeaders.SET_COOKIE type STRING means "Set Refresh Token",
        )
        private val responseCookies: Array<DocumentField> = arrayOf(
            AuthToken.REFRESH_TOKEN_HEADER type STRING means "Refresh Token",
        )

        @Test
        fun `유효하지 않은 RefreshToken으로 인해 토큰 재발급에 실패한다`() {
            postRequest(baseUrl) {
                refreshToken(INVALID_REFRESH_TOKEN)
            }.andExpect {
                status { isForbidden() }
                content { exception(AuthExceptionCode.INVALID_PERMISSION) }
            }.andDo {
                makeFailureDocsWithRefreshToken("TokenReissueApi/Failure") {}
            }
        }

        @Test
        fun `사용자 소유의 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다`() {
            val response = AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
            every { reissueTokenUseCase.invoke(any()) } returns response

            postRequest(baseUrl) {
                refreshToken(REFRESH_TOKEN)
            }.andExpect {
                status { isNoContent() }
                header {
                    string(AuthToken.ACCESS_TOKEN_HEADER, containsString(ACCESS_TOKEN))
                    string(HttpHeaders.SET_COOKIE, containsString(REFRESH_TOKEN))
                }
                cookie { value(AuthToken.REFRESH_TOKEN_HEADER, containsString(REFRESH_TOKEN)) }
            }.andDo {
                makeSuccessDocsWithRefreshToken("TokenReissueApi/Success") {
                    responseHeaders(*responseHeaders)
                    responseCookies(*responseCookies)
                }
            }
        }
    }
}
