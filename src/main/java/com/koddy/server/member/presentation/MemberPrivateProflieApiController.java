package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.OnlyMentee;
import com.koddy.server.global.aop.OnlyMentor;
import com.koddy.server.member.application.usecase.GetMemberPrivateProfileUseCase;
import com.koddy.server.member.application.usecase.query.response.MenteeProfile;
import com.koddy.server.member.application.usecase.query.response.MentorProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 마이페이지(Private) 프로필 조회 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberPrivateProflieApiController {
    private final GetMemberPrivateProfileUseCase getMemberPrivateProfileUseCase;

    @Operation(summary = "멘토 마이페이지 프로필 조회 Endpoint")
    @GetMapping("/mentors/me")
    @OnlyMentor
    public ResponseEntity<MentorProfile> getMentorProfile(
            @Auth final Authenticated authenticated
    ) {
        final MentorProfile response = getMemberPrivateProfileUseCase.getMentorProfile(authenticated.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "멘티 마이페이지 프로필 조회 Endpoint")
    @GetMapping("/mentees/me")
    @OnlyMentee
    public ResponseEntity<MenteeProfile> getMenteeProfile(
            @Auth final Authenticated authenticated
    ) {
        final MenteeProfile response = getMemberPrivateProfileUseCase.getMenteeProfile(authenticated.id());
        return ResponseEntity.ok(response);
    }
}
