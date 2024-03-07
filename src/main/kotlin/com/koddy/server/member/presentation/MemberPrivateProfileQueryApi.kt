package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.member.application.usecase.GetMemberPrivateProfileUseCase
import com.koddy.server.member.application.usecase.query.response.MemberPrivateProfile
import com.koddy.server.member.application.usecase.query.response.MenteePrivateProfile
import com.koddy.server.member.application.usecase.query.response.MentorPrivateProfile
import com.koddy.server.member.domain.model.Role
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-7. 사용자 마이페이지(Private) 프로필 조회 API")
@RestController
@RequestMapping("/api")
class MemberPrivateProfileQueryApi(
    private val getMemberPrivateProfileUseCase: GetMemberPrivateProfileUseCase,
) {
    @Operation(summary = "마이페이지 프로필 조회 Endpoint (@Auth Authorities에 따른 분기)")
    @GetMapping("/members/me")
    fun getPrivateProfile(
        @Auth authenticated: Authenticated,
    ): ResponseEntity<MemberPrivateProfile> {
        val response: MemberPrivateProfile = when (authenticated.isMentor) {
            true -> getMemberPrivateProfileUseCase.getMentorProfile(authenticated.id)
            false -> getMemberPrivateProfileUseCase.getMenteeProfile(authenticated.id)
        }
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "멘토 마이페이지 프로필 조회 Endpoint - Deprecated by requirements")
    @GetMapping("/mentors/me")
    @AccessControl(role = Role.MENTOR)
    fun getMentorPrivateProfile(
        @Auth authenticated: Authenticated,
    ): ResponseEntity<MentorPrivateProfile> {
        val response: MentorPrivateProfile = getMemberPrivateProfileUseCase.getMentorProfile(authenticated.id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "멘티 마이페이지 프로필 조회 Endpoint - Deprecated by requirements")
    @GetMapping("/mentees/me")
    @AccessControl(role = Role.MENTEE)
    fun getMenteePrivateProfile(
        @Auth authenticated: Authenticated,
    ): ResponseEntity<MenteePrivateProfile> {
        val response: MenteePrivateProfile = getMemberPrivateProfileUseCase.getMenteeProfile(authenticated.id)
        return ResponseEntity.ok(response)
    }
}
