package com.koddy.server.auth.infrastructure.social

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthProperties
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthUriGenerator
import com.koddy.server.auth.infrastructure.social.kakao.KakaoOAuthProperties
import com.koddy.server.auth.infrastructure.social.kakao.KakaoOAuthUriGenerator
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthProperties
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthUriGenerator
import com.koddy.server.common.TestEnvironment
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder

@TestEnvironment
@SpringBootTest(
    classes = [
        OAuthUriGeneratorFacade::class,
        GoogleOAuthUriGenerator::class,
        KakaoOAuthUriGenerator::class,
        ZoomOAuthUriGenerator::class,
    ],
)
@EnableConfigurationProperties(
    value = [
        GoogleOAuthProperties::class,
        KakaoOAuthProperties::class,
        ZoomOAuthProperties::class,
    ],
)
@DisplayName("Auth -> OAuthUriGeneratorFacade [구글, 카카오, 줌] 테스트")
internal class OAuthUriGeneratorFacadeTest(
    private val sut: OAuthUriGeneratorFacade,
    private val googleOAuthProperties: GoogleOAuthProperties,
    private val kakaoOAuthProperties: KakaoOAuthProperties,
    private val zoomOAuthProperties: ZoomOAuthProperties,
) {
    @Test
    fun `Google's Authorization Code를 받기 위한 Authorization Code Request URI를 생성한다`() {
        // when
        val uri: String = sut.generate(OAuthProvider.GOOGLE, googleOAuthProperties.redirectUri)

        // then
        val queryParams: MultiValueMap<String, String> = UriComponentsBuilder
            .fromUriString(uri)
            .build()
            .queryParams

        assertSoftly {
            queryParams.getFirst("response_type") shouldBe "code"
            queryParams.getFirst("client_id") shouldBe googleOAuthProperties.clientId
            queryParams.getFirst("scope") shouldBe googleOAuthProperties.scope.joinToString(separator = " ")
            queryParams.getFirst("redirect_uri") shouldBe googleOAuthProperties.redirectUri
            queryParams.getFirst("state") shouldNotBe null
        }
    }

    @Test
    @DisplayName("Kakao's Authorization Code를 받기 위한 Authorization Code Request URI를 생성한다")
    fun kakaoOAuthUri() {
        // when
        val uri: String = sut.generate(OAuthProvider.KAKAO, kakaoOAuthProperties.redirectUri)

        // then
        val queryParams: MultiValueMap<String, String> = UriComponentsBuilder
            .fromUriString(uri)
            .build()
            .queryParams

        assertSoftly {
            queryParams.getFirst("response_type") shouldBe "code"
            queryParams.getFirst("client_id") shouldBe kakaoOAuthProperties.clientId
            queryParams.getFirst("scope") shouldBe kakaoOAuthProperties.scope.joinToString(separator = " ")
            queryParams.getFirst("redirect_uri") shouldBe kakaoOAuthProperties.redirectUri
            queryParams.getFirst("state") shouldNotBe null
        }
    }

    @Test
    @DisplayName("Zoom's Authorization Code를 받기 위한 Authorization Code Request URI를 생성한다")
    fun zoomOAuthUri() {
        // when
        val uri: String = sut.generate(OAuthProvider.ZOOM, zoomOAuthProperties.redirectUri)

        // then
        val queryParams: MultiValueMap<String, String> = UriComponentsBuilder
            .fromUriString(uri)
            .build()
            .queryParams

        assertSoftly {
            queryParams.getFirst("response_type") shouldBe "code"
            queryParams.getFirst("client_id") shouldBe zoomOAuthProperties.clientId
            queryParams.getFirst("redirect_uri") shouldBe zoomOAuthProperties.redirectUri
            queryParams.getFirst("state") shouldNotBe null
        }
    }
}
