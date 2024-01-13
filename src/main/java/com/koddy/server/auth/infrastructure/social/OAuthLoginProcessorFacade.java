package com.koddy.server.auth.infrastructure.social;

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthConnector;
import com.koddy.server.auth.infrastructure.social.kakao.KakaoOAuthConnector;
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthConnector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthLoginProcessorFacade implements OAuthLoginProcessor {
    private final GoogleOAuthConnector googleOAuthConnector;
    private final KakaoOAuthConnector kakaoOAuthConnector;
    private final ZoomOAuthConnector zoomOAuthConnector;

    @Override
    public OAuthUserResponse login(
            final OAuthProvider provider,
            final String code,
            final String redirectUri,
            final String state
    ) {
        return switch (provider) {
            case GOOGLE -> googleOAuthConnector.login(code, redirectUri, state);
            case KAKAO -> kakaoOAuthConnector.login(code, redirectUri, state);
            case ZOOM -> zoomOAuthConnector.login(code, redirectUri, state);
        };
    }
}
