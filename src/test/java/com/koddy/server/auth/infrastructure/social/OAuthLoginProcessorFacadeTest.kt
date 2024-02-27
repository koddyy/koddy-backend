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
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@UnitTestKt
@DisplayName("Auth -> OAuthLoginProcessorFacade [구글, 카카오, 줌] 테스트")
internal class OAuthLoginProcessorFacadeTest {
    private val googleOAuthConnector = mockk<GoogleOAuthConnector>()
    private val kakaoOAuthConnector = mockk<KakaoOAuthConnector>()
    private val zoomOAuthConnector = mockk<ZoomOAuthConnector>()
    private val sut = OAuthLoginProcessorFacade(
        googleOAuthConnector,
        kakaoOAuthConnector,
        zoomOAuthConnector,
    )

    @Test
    fun `Provider별 OAuth 로그인을 진행한다`() {
        // given
        val googleUserResponse: GoogleUserResponse = MENTOR_1.toGoogleUserResponse()
        val kakaoUserResponse: KakaoUserResponse = MENTOR_1.toKakaoUserResponse()
        val zoomUserResponse: ZoomUserResponse = MENTOR_1.toZoomUserResponse()

        every { googleOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns googleUserResponse
        every { kakaoOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns kakaoUserResponse
        every { zoomOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE) } returns zoomUserResponse

        // when
        val google: OAuthUserResponse = sut.login(OAuthProvider.GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE)
        val kakao: OAuthUserResponse = sut.login(OAuthProvider.KAKAO, AUTHORIZATION_CODE, REDIRECT_URI, STATE)
        val zoom: OAuthUserResponse = sut.login(OAuthProvider.ZOOM, AUTHORIZATION_CODE, REDIRECT_URI, STATE)

        // when - then
        assertSoftly {
            google shouldBe googleUserResponse
            kakao shouldBe kakaoUserResponse
            zoom shouldBe zoomUserResponse
        }
    }
}
