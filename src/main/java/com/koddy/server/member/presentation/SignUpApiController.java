package com.koddy.server.member.presentation;

import com.koddy.server.member.application.usecase.SimpleSignUpUseCase;
import com.koddy.server.member.presentation.dto.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class SignUpApiController {
    private final SimpleSignUpUseCase simpleSignUpUseCase;

    @PostMapping("/mentor")
    public ResponseEntity<SignUpResponse> mentorSimpleSignUp() {
        return null;
    }

    @PostMapping("/mentee")
    public ResponseEntity<SignUpResponse> menteeSimpleSignUp() {
        return null;
    }
}
