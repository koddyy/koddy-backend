package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.global.query.PageResponse
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.member.application.usecase.MentorMainSearchUseCase
import com.koddy.server.member.application.usecase.query.GetAppliedMentees
import com.koddy.server.member.application.usecase.query.response.AppliedCoffeeChatsByMenteeResponse
import com.koddy.server.member.application.usecase.query.response.MenteeSimpleSearchProfile
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.presentation.request.GetMenteesByConditionRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-9. 신청온 커피챗, 멘티 둘러보기 조회 API")
@RestController
@RequestMapping("/api/mentees")
class MentorMainSearchApi(
    private val mentorMainSearchUseCase: MentorMainSearchUseCase,
) {
    @Operation(summary = "멘티로부터 신청온 커피챗 조회 Endpoint (멘토 전용)")
    @GetMapping("/applied-coffeechats")
    @AccessControl(role = Role.MENTOR)
    fun getAppliedMentees(
        @Auth authenticated: Authenticated,
        @RequestParam(defaultValue = "3") limit: Int,
    ): ResponseEntity<PageResponse<List<AppliedCoffeeChatsByMenteeResponse>>> {
        val result: PageResponse<List<AppliedCoffeeChatsByMenteeResponse>> =
            mentorMainSearchUseCase.getAppliedMentees(
                GetAppliedMentees(
                    authenticated.id,
                    limit,
                ),
            )
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "멘티 둘러보기 Endpoint")
    @GetMapping
    fun getMenteesByCondition(
        @ModelAttribute @Valid request: GetMenteesByConditionRequest,
    ): ResponseEntity<SliceResponse<List<MenteeSimpleSearchProfile>>> {
        val result: SliceResponse<List<MenteeSimpleSearchProfile>> = mentorMainSearchUseCase.getMenteesByCondition(
            request.toQuery(),
        )
        return ResponseEntity.ok(result)
    }
}
