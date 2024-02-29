package com.koddy.server.auth.infrastructure.social

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthConnector
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse
import com.koddy.server.auth.infrastructure.social.kakao.KakaoOAuthConnector
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoUserResponse
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthConnector
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.utils.OAuthUtils.AUTHORIZATION_CODE
import com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI
import com.koddy.server.common.utils.OAuthUtils.STATE
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Auth -> OAuthLoginProcessorFacade [구글, 카카오, 줌] 테스트")
internal class OAuthLoginProcessorFacadeTest : DescribeSpec({
    val googleOAuthConnector = mockk<GoogleOAuthConnector>()
    val kakaoOAuthConnector = mockk<KakaoOAuthConnector>()
    val zoomOAuthConnector = mockk<ZoomOAuthConnector>()
    val sut = OAuthLoginProcessorFacade(
        googleOAuthConnector,
        kakaoOAuthConnector,
        zoomOAuthConnector,
    )

    describe("OAuthLoginProcessorFacade's login") {
        val googleUserResponse: GoogleUserResponse = MENTOR_1.toGoogleUserResponse()
        val kakaoUserResponse: KakaoUserResponse = MENTOR_1.toKakaoUserResponse()
        val zoomUserResponse: ZoomUserResponse = MENTOR_1.toZoomUserResponse()

        every { googleOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns googleUserResponse
        every { kakaoOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns kakaoUserResponse
        every { zoomOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns zoomUserResponse

        context("Google OAuth Provider에 대해서") {
            it("로그인을 진행한다") {
                val result: OAuthUserResponse = sut.login(OAuthProvider.GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE)

                verify(exactly = 1) { googleOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) }
                verify {
                    kakaoOAuthConnector wasNot Called
                    zoomOAuthConnector wasNot Called
                }
                result shouldBe googleUserResponse
            }
        }

        context("Kakao OAuth Provider에 대해서") {
            it("로그인을 진행한다") {
                val result: OAuthUserResponse = sut.login(OAuthProvider.KAKAO, AUTHORIZATION_CODE, REDIRECT_URI, STATE)

                verify(exactly = 1) { kakaoOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) }
                verify {
                    googleOAuthConnector wasNot Called
                    zoomOAuthConnector wasNot Called
                }
                result shouldBe kakaoUserResponse
            }
        }

        context("Zoom OAuth Provider에 대해서") {
            it("로그인을 진행한다") {
                val result: OAuthUserResponse = sut.login(OAuthProvider.ZOOM, AUTHORIZATION_CODE, REDIRECT_URI, STATE)

                verify(exactly = 1) { zoomOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) }
                verify {
                    googleOAuthConnector wasNot Called
                    kakaoOAuthConnector wasNot Called
                }
                result shouldBe zoomUserResponse
            }
        }
    }
})
