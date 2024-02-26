package com.koddy.server.member.presentation

import com.koddy.server.member.application.usecase.GetMemberBasicProfileUseCase
import com.koddy.server.member.application.usecase.query.response.MenteeBasicProfile
import com.koddy.server.member.application.usecase.query.response.MentorBasicProfile
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-8. 사용자 기본(Public) 프로필 조회 API")
@RestController
@RequestMapping("/api")
class MemberBasicProfileQueryApi(
    private val getMemberBasicProfileUseCase: GetMemberBasicProfileUseCase,
) {
    @Operation(summary = "멘토 기본 프로필 조회 Endpoint")
    @GetMapping("/mentors/{mentorId}")
    fun getMentorProfile(
        @PathVariable mentorId: Long,
    ): ResponseEntity<MentorBasicProfile> {
        val response: MentorBasicProfile = getMemberBasicProfileUseCase.getMentorProfile(mentorId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "멘티 기본 프로필 조회 Endpoint")
    @GetMapping("/mentees/{menteeId}")
    fun getMenteeProfile(
        @PathVariable menteeId: Long,
    ): ResponseEntity<MenteeBasicProfile> {
        val response: MenteeBasicProfile = getMemberBasicProfileUseCase.getMenteeProfile(menteeId)
        return ResponseEntity.ok(response)
    }
}
