package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.global.aop.DailyMailAuthLimit;
import com.koddy.server.global.utils.UniversityInfo;
import com.koddy.server.member.application.usecase.AuthenticationMentorUnivUseCase;
import com.koddy.server.member.application.usecase.command.AuthenticationConfirmWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithProofDataCommand;
import com.koddy.server.member.presentation.request.AuthenticationConfirmWithMailRequest;
import com.koddy.server.member.presentation.request.AuthenticationWithMailRequest;
import com.koddy.server.member.presentation.request.AuthenticationWithProofDataRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "2-5. 멘토 학교 인증 API")
@RestController
@RequestMapping("/api/mentors/me/univ")
@RequiredArgsConstructor
public class AuthenticationMentorUnivApiController {
    private final AuthenticationMentorUnivUseCase authenticationMentorUnivUseCase;

    @Operation(summary = "메일 인증 시도 Endpoint")
    @PostMapping("/mail")
    @AccessControl(role = MENTOR)
    @DailyMailAuthLimit
    public ResponseEntity<Void> authWithMail(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final AuthenticationWithMailRequest request
    ) {
        UniversityInfo.validateDomain(authenticated, request.schoolMail());
        authenticationMentorUnivUseCase.authWithMail(new AuthenticationWithMailCommand(
                authenticated.id(),
                request.schoolMail()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "메일 인증 확인 Endpoint")
    @PostMapping("/mail/confirm")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> confirmMailAuthCode(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final AuthenticationConfirmWithMailRequest request
    ) {
        UniversityInfo.validateDomain(authenticated, request.schoolMail());
        authenticationMentorUnivUseCase.confirmMailAuthCode(new AuthenticationConfirmWithMailCommand(
                authenticated.id(),
                request.schoolMail(),
                request.authCode()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "증명자료 인증 시도 Endpoint")
    @PostMapping("/proof-data")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> authWithProofData(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final AuthenticationWithProofDataRequest request
    ) {
        authenticationMentorUnivUseCase.authWithProofData(new AuthenticationWithProofDataCommand(
                authenticated.id(),
                request.proofDataUploadUrl()
        ));
        return ResponseEntity.noContent().build();
    }
}
