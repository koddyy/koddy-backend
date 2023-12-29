package com.koddy.server.member.presentation;

import com.koddy.server.member.application.usecase.SimpleSignUpUseCase;
import com.koddy.server.member.application.usecase.command.SimpleSignUpCommand;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.presentation.dto.request.SignUpRequest;
import com.koddy.server.member.presentation.dto.response.SignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class SignUpApiController {
    private final SimpleSignUpUseCase simpleSignUpUseCase;

    @PostMapping
    public ResponseEntity<SignUpResponse> simpleSignUp(@RequestBody @Valid final SignUpRequest request) {
        final Long memberId = simpleSignUpUseCase.invoke(new SimpleSignUpCommand(
                Email.init(request.email()),
                request.password(),
                request.type()
        ));
        return ResponseEntity.ok(new SignUpResponse(memberId));
    }
}
