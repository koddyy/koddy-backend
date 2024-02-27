package com.koddy.server.auth.infrastructure.social

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthConnector
import com.koddy.server.auth.infrastructure.social.kakao.KakaoOAuthConnector
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthConnector
import org.springframework.stereotype.Component

@Component
class OAuthLoginProcessorFacade(
    private val googleOAuthConnector: GoogleOAuthConnector,
    private val kakaoOAuthConnector: KakaoOAuthConnector,
    private val zoomOAuthConnector: ZoomOAuthConnector,
) : OAuthLoginProcessor {
    override fun login(
        provider: OAuthProvider,
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthUserResponse =
        when (provider) {
            OAuthProvider.GOOGLE -> googleOAuthConnector.login(code, redirectUri, state)
            OAuthProvider.KAKAO -> kakaoOAuthConnector.login(code, redirectUri, state)
            OAuthProvider.ZOOM -> zoomOAuthConnector.login(code, redirectUri, state)
        }
}
