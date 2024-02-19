package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.member.application.usecase.GetMemberPrivateProfileUseCase;
import com.koddy.server.member.application.usecase.query.response.MemberPrivateProfile;
import com.koddy.server.member.application.usecase.query.response.MenteePrivateProfile;
import com.koddy.server.member.application.usecase.query.response.MentorPrivateProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTEE;
import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "2-7. 사용자 마이페이지(Private) 프로필 조회 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberPrivateProflieQueryApi {
    private final GetMemberPrivateProfileUseCase getMemberPrivateProfileUseCase;

    @Operation(summary = "마이페이지 프로필 조회 Endpoint (@Auth Authorities에 따른 분기)")
    @GetMapping("/members/me")
    public ResponseEntity<MemberPrivateProfile> getProfile(
            @Auth final Authenticated authenticated
    ) {
        final MemberPrivateProfile response = authenticated.isMentor()
                ? getMemberPrivateProfileUseCase.getMentorProfile(authenticated.id)
                : getMemberPrivateProfileUseCase.getMenteeProfile(authenticated.id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "멘토 마이페이지 프로필 조회 Endpoint - Deprecated by requirements")
    @GetMapping("/mentors/me")
    @AccessControl(role = MENTOR)
    public ResponseEntity<MentorPrivateProfile> getMentorProfile(
            @Auth final Authenticated authenticated
    ) {
        final MentorPrivateProfile response = getMemberPrivateProfileUseCase.getMentorProfile(authenticated.id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "멘티 마이페이지 프로필 조회 Endpoint - Deprecated by requirements")
    @GetMapping("/mentees/me")
    @AccessControl(role = MENTEE)
    public ResponseEntity<MenteePrivateProfile> getMenteeProfile(
            @Auth final Authenticated authenticated
    ) {
        final MenteePrivateProfile response = getMemberPrivateProfileUseCase.getMenteeProfile(authenticated.id);
        return ResponseEntity.ok(response);
    }
}
