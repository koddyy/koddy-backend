package com.koddy.server.auth.infrastructure.social

import com.koddy.server.auth.application.adapter.OAuthUriGenerator
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthUriGenerator
import com.koddy.server.auth.infrastructure.social.kakao.KakaoOAuthUriGenerator
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthUriGenerator
import org.springframework.stereotype.Component

@Component
class OAuthUriGeneratorFacade(
    private val googleOAuthUriGenerator: GoogleOAuthUriGenerator,
    private val kakaoOAuthUriGenerator: KakaoOAuthUriGenerator,
    private val zoomOAuthUriGenerator: ZoomOAuthUriGenerator,
) : OAuthUriGenerator {
    override fun generate(
        provider: OAuthProvider,
        redirectUri: String,
    ): String {
        return when (provider) {
            OAuthProvider.GOOGLE -> googleOAuthUriGenerator.generate(redirectUri)
            OAuthProvider.KAKAO -> kakaoOAuthUriGenerator.generate(redirectUri)
            OAuthProvider.ZOOM -> zoomOAuthUriGenerator.generate(redirectUri)
        }
    }
}
