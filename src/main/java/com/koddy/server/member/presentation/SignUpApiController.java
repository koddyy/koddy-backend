package com.koddy.server.member.presentation;

import com.koddy.server.global.dto.ResponseWrapper;
import com.koddy.server.member.application.usecase.DuplicateCheckUseCase;
import com.koddy.server.member.application.usecase.SimpleSignUpUseCase;
import com.koddy.server.member.application.usecase.command.SimpleSignUpCommand;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.presentation.dto.request.EmailDuplicateCheckRequest;
import com.koddy.server.member.presentation.dto.request.SignUpRequest;
import com.koddy.server.member.presentation.dto.response.SignUpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 생성 관련 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class SignUpApiController {
    private final DuplicateCheckUseCase duplicateCheckUseCase;
    private final SimpleSignUpUseCase simpleSignUpUseCase;

    @Operation(summary = "이메일 중복 체크 Endpoint")
    @PostMapping("/duplicate/email")
    public ResponseEntity<ResponseWrapper<Boolean>> checkEmail(
            @RequestBody @Valid final EmailDuplicateCheckRequest request
    ) {
        final boolean result = duplicateCheckUseCase.isEmailUsable(request.value());
        return ResponseEntity.ok(ResponseWrapper.from(result));
    }

    @Operation(summary = "사용자 간편 회원가입 Endpoint")
    @PostMapping
    public ResponseEntity<SignUpResponse> simpleSignUp(
            @RequestBody @Valid final SignUpRequest request
    ) {
        final Long memberId = simpleSignUpUseCase.invoke(new SimpleSignUpCommand(
                Email.init(request.email()),
                request.password(),
                request.type()
        ));
        return ResponseEntity.ok(new SignUpResponse(memberId));
    }
}
