package com.koddy.server.auth.presentation;

import com.koddy.server.auth.application.usecase.GetOAuthLinkUseCase;
import com.koddy.server.auth.application.usecase.LogoutUseCase;
import com.koddy.server.auth.application.usecase.OAuthLoginUseCase;
import com.koddy.server.auth.application.usecase.command.LogoutCommand;
import com.koddy.server.auth.application.usecase.command.OAuthLoginCommand;
import com.koddy.server.auth.application.usecase.query.GetOAuthLink;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.presentation.dto.request.OAuthLoginRequest;
import com.koddy.server.auth.presentation.dto.response.LoginResponse;
import com.koddy.server.auth.utils.TokenResponseWriter;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "1-1. OAuth 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthApiController {
    private final GetOAuthLinkUseCase getOAuthLinkUseCase;
    private final OAuthLoginUseCase oAuthLoginUseCase;
    private final TokenResponseWriter tokenResponseWriter;
    private final LogoutUseCase logoutUseCase;

    @Operation(summary = "Provider별 OAuth 인증을 위한 URL을 받는 EndPoint")
    @GetMapping(value = "/access/{provider}", params = {"redirectUri"})
    public ResponseEntity<ResponseWrapper<String>> queryOAuthLink(
            @PathVariable final String provider,
            @RequestParam final String redirectUri
    ) {
        final String oAuthLink = getOAuthLinkUseCase.invoke(new GetOAuthLink(
                OAuthProvider.from(provider),
                redirectUri
        ));
        return ResponseEntity.ok(ResponseWrapper.from(oAuthLink));
    }

    @Operation(summary = "Authorization Code를 통해서 Provider별 인증을 위한 EndPoint")
    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable final String provider,
            @RequestBody @Valid final OAuthLoginRequest request,
            final HttpServletResponse response
    ) {
        final AuthMember authMember = oAuthLoginUseCase.invoke(new OAuthLoginCommand(
                OAuthProvider.from(provider),
                request.authorizationCode(),
                request.redirectUri(),
                request.state()
        ));
        tokenResponseWriter.applyToken(response, authMember.token());

        return ResponseEntity.ok(new LoginResponse(
                authMember.id(),
                authMember.name(),
                authMember.profileImageUrl()
        ));
    }

    @Operation(summary = "로그아웃 EndPoint")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Auth final Authenticated authenticated) {
        logoutUseCase.invoke(new LogoutCommand(authenticated.id()));
        return ResponseEntity.noContent().build();
    }
}
