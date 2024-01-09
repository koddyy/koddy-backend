package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.member.application.usecase.CompleteProfileUseCase;
import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand;
import com.koddy.server.member.presentation.dto.request.CompleteMenteeProfileRequest;
import com.koddy.server.member.presentation.dto.request.CompleteMentorProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 프로필 완성 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CompleteAccountApiController {
    private final CompleteProfileUseCase completeProfileUseCase;

    @Operation(summary = "멘토 프로필 완성 Endpoint")
    @PatchMapping("/mentors/me/complete")
    public ResponseEntity<Void> completeMentor(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final CompleteMentorProfileRequest request
    ) {
        completeProfileUseCase.completeMentor(new CompleteMentorProfileCommand(
                authenticated.id(),
                request.introduction(),
                request.toSchedules()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멘티 프로필 완성 Endpoint")
    @PatchMapping("/mentees/me/complete")
    public ResponseEntity<Void> completeMentee(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final CompleteMenteeProfileRequest request
    ) {
        completeProfileUseCase.completeMentee(new CompleteMenteeProfileCommand(
                authenticated.id(),
                request.introduction()
        ));
        return ResponseEntity.noContent().build();
    }
}
