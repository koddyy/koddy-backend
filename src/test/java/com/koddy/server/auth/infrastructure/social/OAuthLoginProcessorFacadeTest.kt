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
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.utils.OAuthDummy.AUTHORIZATION_CODE
import com.koddy.server.common.utils.OAuthDummy.REDIRECT_URI
import com.koddy.server.common.utils.OAuthDummy.STATE
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Auth -> OAuthLoginProcessorFacade [구글, 카카오, 줌] 테스트")
internal class OAuthLoginProcessorFacadeTest : FeatureSpec({
    val googleOAuthConnector = mockk<GoogleOAuthConnector>()
    val kakaoOAuthConnector = mockk<KakaoOAuthConnector>()
    val zoomOAuthConnector = mockk<ZoomOAuthConnector>()
    val sut = OAuthLoginProcessorFacade(
        googleOAuthConnector,
        kakaoOAuthConnector,
        zoomOAuthConnector,
    )

    feature("OAuthLoginProcessorFacade's login") {
        val googleUserResponse: GoogleUserResponse = mentorFixture(id = 1L).toGoogleUserResponse()
        val kakaoUserResponse: KakaoUserResponse = mentorFixture(id = 1L).toKakaoUserResponse()
        val zoomUserResponse: ZoomUserResponse = mentorFixture(id = 1L).toZoomUserResponse()

        every { googleOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns googleUserResponse
        every { kakaoOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns kakaoUserResponse
        every { zoomOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns zoomUserResponse

        scenario("Google OAuth Provider을 활용해서 로그인을 진행한다") {
            val result: OAuthUserResponse = sut.login(OAuthProvider.GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE)

            verify(exactly = 1) { googleOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) }
            verify {
                kakaoOAuthConnector wasNot Called
                zoomOAuthConnector wasNot Called
            }
            result shouldBe googleUserResponse
        }

        scenario("Kakao OAuth Provider을 활용해서 로그인을 진행한다") {
            val result: OAuthUserResponse = sut.login(OAuthProvider.KAKAO, AUTHORIZATION_CODE, REDIRECT_URI, STATE)

            verify(exactly = 1) { kakaoOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) }
            verify {
                googleOAuthConnector wasNot Called
                zoomOAuthConnector wasNot Called
            }
            result shouldBe kakaoUserResponse
        }

        scenario("Zoom OAuth Provider을 활용해서 로그인을 진행한다") {
            val result: OAuthUserResponse = sut.login(OAuthProvider.ZOOM, AUTHORIZATION_CODE, REDIRECT_URI, STATE)

            verify(exactly = 1) { zoomOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) }
            verify {
                googleOAuthConnector wasNot Called
                kakaoOAuthConnector wasNot Called
            }
            result shouldBe zoomUserResponse
        }
    }
})
