package com.koddy.server.global.annotation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.utils.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.koddy.server.auth.domain.model.Authenticated.SESSION_KEY;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.auth.utils.RequestTokenExtractor.extractAccessToken;

@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {
    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class);
    }

    @Override
    public Authenticated resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        // 토큰 버전
        return getTokenPayload(webRequest);

        // 세션 버전
//        return getSessionPayload(webRequest);
    }

    /**
     * 토큰 버전
     */
    private Authenticated getTokenPayload(final NativeWebRequest webRequest) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final String accessToken = getAccessToken(request);
        return new Authenticated(tokenProvider.getId(accessToken), tokenProvider.getRoles(accessToken));
    }

    private String getAccessToken(final HttpServletRequest request) {
        final String accessToken = extractAccessToken(request)
                .orElseThrow(() -> new AuthException(INVALID_PERMISSION));
        tokenProvider.validateToken(accessToken);
        return accessToken;
    }

    /**
     * 세션 버전
     */
    private Authenticated getSessionPayload(final NativeWebRequest webRequest) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final HttpSession session = request.getSession(false);
        validateSessionPayload(session);
        return (Authenticated) session.getAttribute(SESSION_KEY);
    }

    private void validateSessionPayload(final HttpSession session) {
        if (session == null || session.getAttribute(SESSION_KEY) == null) {
            throw new AuthException(INVALID_PERMISSION);
        }
    }
}
