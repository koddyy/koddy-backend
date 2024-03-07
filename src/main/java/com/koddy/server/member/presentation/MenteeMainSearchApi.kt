package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.global.query.PageResponse
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.member.application.usecase.MenteeMainSearchUseCase
import com.koddy.server.member.application.usecase.query.GetSuggestedMentors
import com.koddy.server.member.application.usecase.query.response.MentorSimpleSearchProfile
import com.koddy.server.member.application.usecase.query.response.SuggestedCoffeeChatsByMentorResponse
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.presentation.request.LookAroundMentorsByConditionRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "2-10. 제안온 커피챗, 멘토 둘러보기 조회 API")
@RestController
@RequestMapping("/api/mentors")
class MenteeMainSearchApi(
    private val menteeMainSearchUseCase: MenteeMainSearchUseCase,
) {
    @Operation(summary = "멘토로부터 제안온 커피챗 조회 Endpoint (멘티 전용)")
    @GetMapping("/suggested-coffeechats")
    @AccessControl(role = Role.MENTEE)
    fun getSuggestedMentors(
        @Auth authenticated: Authenticated,
        @RequestParam(defaultValue = "3") limit: Int,
    ): ResponseEntity<PageResponse<List<SuggestedCoffeeChatsByMentorResponse>>> {
        val result: PageResponse<List<SuggestedCoffeeChatsByMentorResponse>> =
            menteeMainSearchUseCase.getSuggestedMentors(
                GetSuggestedMentors(
                    menteeId = authenticated.id,
                    limit = limit,
                ),
            )
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "멘토 둘러보기 Endpoint")
    @GetMapping
    fun lookAroundMentorsByCondition(
        @ModelAttribute @Valid request: LookAroundMentorsByConditionRequest,
    ): ResponseEntity<SliceResponse<List<MentorSimpleSearchProfile>>> {
        val result: SliceResponse<List<MentorSimpleSearchProfile>> = menteeMainSearchUseCase.lookAroundMentorsByCondition(request.toQuery())
        return ResponseEntity.ok(result)
    }
}
