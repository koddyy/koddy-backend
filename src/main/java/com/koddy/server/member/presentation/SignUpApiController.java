package com.koddy.server.member.presentation;

import com.koddy.server.member.application.usecase.SignUpUsecase;
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand;
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import com.koddy.server.member.presentation.dto.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMentorRequest;
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

@Tag(name = "사용자 회원가입 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignUpApiController {
    private final SignUpUsecase signUpUsecase;

    @Operation(summary = "멘토 회원가입 Endpoint")
    @PostMapping("/mentors")
    public ResponseEntity<SignUpResponse> signUpMentor(
            @RequestBody @Valid final SignUpMentorRequest request
    ) {
        final Long memberId = signUpUsecase.signUpMentor(new SignUpMentorCommand(
                Email.from(request.email()),
                request.name(),
                request.profileImageUrl(),
                request.introduction(),
                Language.of(request.languages()),
                new UniversityProfile(request.school(), request.major(), request.enteredIn()),
                request.toSchedules()
        ));
        return ResponseEntity.ok(new SignUpResponse(memberId));
    }

    @Operation(summary = "멘티 회원가입 Endpoint")
    @PostMapping("/mentees")
    public ResponseEntity<SignUpResponse> signUpMentee(
            @RequestBody @Valid final SignUpMenteeRequest request
    ) {
        final Long memberId = signUpUsecase.signUpMentee(new SignUpMenteeCommand(
                Email.from(request.email()),
                request.name(),
                request.profileImageUrl(),
                Nationality.from(request.nationality()),
                request.introduction(),
                Language.of(request.languages()),
                new Interest(request.interestSchool(), request.interestMajor())
        ));
        return ResponseEntity.ok(new SignUpResponse(memberId));
    }
}
