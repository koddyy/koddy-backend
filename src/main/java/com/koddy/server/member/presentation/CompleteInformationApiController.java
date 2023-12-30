package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.OnlyMentee;
import com.koddy.server.global.aop.OnlyMentor;
import com.koddy.server.member.application.usecase.CompleteInformationUseCase;
import com.koddy.server.member.application.usecase.command.CompleteMenteeCommand;
import com.koddy.server.member.application.usecase.command.CompleteMentorCommand;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import com.koddy.server.member.presentation.dto.request.CompleteMenteeRequest;
import com.koddy.server.member.presentation.dto.request.CompleteMentorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 부가정보 기입 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class CompleteInformationApiController {
    private final CompleteInformationUseCase completeInformationUseCase;

    @Operation(summary = "멘토 부가정보 기입 Endpoint")
    @OnlyMentor
    @PostMapping("/mentor")
    public ResponseEntity<Void> completeMentor(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final CompleteMentorRequest request
    ) {
        completeInformationUseCase.completeMentor(new CompleteMentorCommand(
                authenticated.id(),
                request.name(),
                request.nationality(),
                request.profileUploadUrl(),
                request.languages(),
                new UniversityProfile(request.school(), request.major(), request.grade()),
                request.meetingUrl(),
                request.introduction(),
                request.toSchedules()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멘티 부가정보 기입 Endpoint")
    @OnlyMentee
    @PostMapping("/mentee")
    public ResponseEntity<Void> completeMentee(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final CompleteMenteeRequest request
    ) {
        completeInformationUseCase.completeMentee(new CompleteMenteeCommand(
                authenticated.id(),
                request.name(),
                request.nationality(),
                request.profileUploadUrl(),
                request.languages(),
                new Interest(request.interestSchool(), request.interestMajor())
        ));
        return ResponseEntity.noContent().build();
    }
}
