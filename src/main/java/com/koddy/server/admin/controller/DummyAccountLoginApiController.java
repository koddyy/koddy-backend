package com.koddy.server.admin.controller;

import com.koddy.server.admin.service.DummyAccountLoginService;
import com.koddy.server.admin.utils.NotProvidedInProduction;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.member.domain.model.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/dummy/login")
@RequiredArgsConstructor
public class DummyAccountLoginApiController {
    private final DummyAccountLoginService dummyAccountLoginService;

    public record LoginRequest(
            String email,
            String password
    ) {
    }

    @PostMapping
    @NotProvidedInProduction
    public ResponseEntity<AuthMember> login(
            @RequestBody final LoginRequest request
    ) {
        return ResponseEntity.ok(dummyAccountLoginService.invoke(Email.from(request.email()), request.password()));
    }
}
