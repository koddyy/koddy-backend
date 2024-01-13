package com.koddy.server.auth.infrastructure.social;

import com.koddy.server.auth.application.adapter.OAuthUriGenerator;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthUriGenerator;
import com.koddy.server.auth.infrastructure.social.kakao.KakaoOAuthUriGenerator;
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthUriGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthUriGeneratorFacade implements OAuthUriGenerator {
    private final GoogleOAuthUriGenerator googleOAuthUriGenerator;
    private final KakaoOAuthUriGenerator kakaoOAuthUriGenerator;
    private final ZoomOAuthUriGenerator zoomOAuthUriGenerator;

    @Override
    public String generate(final OAuthProvider provider, final String redirectUri) {
        return switch (provider) {
            case GOOGLE -> googleOAuthUriGenerator.generate(redirectUri);
            case KAKAO -> kakaoOAuthUriGenerator.generate(redirectUri);
            case ZOOM -> zoomOAuthUriGenerator.generate(redirectUri);
        };
    }
}
