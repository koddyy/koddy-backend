package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.global.query.PageResponse;
import com.koddy.server.global.query.SliceResponse;
import com.koddy.server.member.application.usecase.MentorMainSearchUseCase;
import com.koddy.server.member.application.usecase.query.GetAppliedMentees;
import com.koddy.server.member.application.usecase.query.response.AppliedCoffeeChatsByMenteeResponse;
import com.koddy.server.member.application.usecase.query.response.MenteeSimpleSearchProfile;
import com.koddy.server.member.presentation.request.GetMenteesByConditionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "2-9. 신청온 커피챗, 멘티 둘러보기 조회 API")
@RestController
@RequestMapping("/api/mentees")
@RequiredArgsConstructor
public class MentorMainSearchApi {
    private final MentorMainSearchUseCase mentorMainSearchUseCase;

    @Operation(summary = "멘티로부터 신청온 커피챗 조회 Endpoint (멘토 전용)")
    @GetMapping("/applied-coffeechats")
    @AccessControl(role = MENTOR)
    public ResponseEntity<PageResponse<List<AppliedCoffeeChatsByMenteeResponse>>> getAppliedMentees(
            @Auth final Authenticated authenticated,
            @RequestParam(defaultValue = "3") final int limit
    ) {
        final PageResponse<List<AppliedCoffeeChatsByMenteeResponse>> result = mentorMainSearchUseCase.getAppliedMentees(
                new GetAppliedMentees(authenticated.id, limit)
        );
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "멘티 둘러보기 Endpoint")
    @GetMapping
    public ResponseEntity<SliceResponse<List<MenteeSimpleSearchProfile>>> getMenteesByCondition(
            @ModelAttribute @Valid final GetMenteesByConditionRequest request
    ) {
        final SliceResponse<List<MenteeSimpleSearchProfile>> result = mentorMainSearchUseCase.getMenteesByCondition(request.toQuery());
        return ResponseEntity.ok(result);
    }
}
