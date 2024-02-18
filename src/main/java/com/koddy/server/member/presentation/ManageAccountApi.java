package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.presentation.response.LoginResponse;
import com.koddy.server.auth.utils.TokenResponseWriter;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.member.application.usecase.DeleteMemberUseCase;
import com.koddy.server.member.application.usecase.SignUpUsecase;
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand;
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand;
import com.koddy.server.member.presentation.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.request.SignUpMentorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "2-1. 사용자 계정 관리 (회원가입 + 로그인/탈퇴) API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ManageAccountApi {
    private final SignUpUsecase signUpUsecase;
    private final TokenResponseWriter tokenResponseWriter;
    private final DeleteMemberUseCase deleteMemberUseCase;

    @Operation(summary = "멘토 회원가입 + 로그인 Endpoint")
    @PostMapping("/mentors")
    public ResponseEntity<LoginResponse> signUpMentor(
            @RequestBody @Valid final SignUpMentorRequest request,
            final HttpServletResponse response
    ) {
        final AuthMember authMember = signUpUsecase.signUpMentor(new SignUpMentorCommand(
                request.toSocialPlatform(),
                request.name(),
                request.toLanguages(),
                request.toUniversityProfile()
        ));
        tokenResponseWriter.applyToken(response, authMember.token());

        return ResponseEntity.ok(new LoginResponse(
                authMember.id(),
                authMember.name()
        ));
    }

    @Operation(summary = "멘티 회원가입 + 로그인 Endpoint")
    @PostMapping("/mentees")
    public ResponseEntity<LoginResponse> signUpMentee(
            @RequestBody @Valid final SignUpMenteeRequest request,
            final HttpServletResponse response
    ) {
        final AuthMember authMember = signUpUsecase.signUpMentee(new SignUpMenteeCommand(
                request.toSocialPlatform(),
                request.name(),
                request.toNationality(),
                request.toLanguages(),
                request.toInterest()
        ));
        tokenResponseWriter.applyToken(response, authMember.token());

        return ResponseEntity.ok(new LoginResponse(
                authMember.id(),
                authMember.name()
        ));
    }

    @Operation(summary = "사용자 탈퇴 Endpoint")
    @DeleteMapping("/members")
    public ResponseEntity<Void> delete(
            @Auth final Authenticated authenticated
    ) {
        deleteMemberUseCase.invoke(authenticated.id());
        return ResponseEntity.noContent().build();
    }
}