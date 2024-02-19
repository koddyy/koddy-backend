package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.coffeechat.application.usecase.ManageMeetingLinkUseCase;
import com.koddy.server.coffeechat.application.usecase.command.CreateMeetingLinkCommand;
import com.koddy.server.coffeechat.application.usecase.command.DeleteMeetingLinkCommand;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;
import com.koddy.server.coffeechat.presentation.request.CreateMeetingLinkRequest;
import com.koddy.server.coffeechat.presentation.response.CreateMeetingLinkResponse;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "4-1. 커피챗 링크 생성/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth/{provider}/meetings")
public class ManageMeetingLinkApi {
    private final ManageMeetingLinkUseCase manageMeetingLinkUseCase;

    @Operation(summary = "커피챗 링크 생성 Endpoint")
    @PostMapping
    @AccessControl(role = MENTOR)
    public ResponseEntity<CreateMeetingLinkResponse> create(
            @Auth final Authenticated authenticated,
            @PathVariable final String provider,
            @RequestBody final CreateMeetingLinkRequest request
    ) {
        final MeetingLinkResponse result = manageMeetingLinkUseCase.create(new CreateMeetingLinkCommand(
                authenticated.id,
                OAuthProvider.from(provider),
                MeetingLinkProvider.from(provider),
                request.authorizationCode(),
                request.redirectUri(),
                request.state(),
                request.topic(),
                request.toStart(),
                request.toEnd()
        ));
        return ResponseEntity.ok(new CreateMeetingLinkResponse(
                result.id(),
                result.hostEmail(),
                result.topic(),
                result.joinUrl(),
                result.duration()
        ));
    }

    @Operation(summary = "커피챗 링크 삭제 Endpoint")
    @DeleteMapping("/{meetingId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> delete(
            @Auth final Authenticated authenticated,
            @PathVariable final String provider,
            @PathVariable final String meetingId
    ) {
        manageMeetingLinkUseCase.delete(new DeleteMeetingLinkCommand(MeetingLinkProvider.from(provider), meetingId));
        return ResponseEntity.noContent().build();
    }
}
