package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.global.query.PageResponse;
import com.koddy.server.global.query.SliceResponse;
import com.koddy.server.member.application.usecase.MenteeMainSearchUseCase;
import com.koddy.server.member.application.usecase.query.GetSuggestedMentors;
import com.koddy.server.member.application.usecase.query.response.MentorSimpleSearchProfile;
import com.koddy.server.member.application.usecase.query.response.SuggestedCoffeeChatsByMentorResponse;
import com.koddy.server.member.presentation.dto.request.GetMentorsByConditionRequest;
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

import static com.koddy.server.member.domain.model.Role.MENTEE;

@Tag(name = "2-10. 제안온 커피챗, 멘토 둘러보기 조회 API")
@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MenteeMainSearchApiController {
    private final MenteeMainSearchUseCase menteeMainSearchUseCase;

    @Operation(summary = "멘토로부터 제안온 커피챗 조회 Endpoint (멘티 전용)")
    @GetMapping("/suggested-coffeechats")
    @AccessControl(role = MENTEE)
    public ResponseEntity<PageResponse<List<SuggestedCoffeeChatsByMentorResponse>>> getSuggestedMentors(
            @Auth final Authenticated authenticated,
            @RequestParam(defaultValue = "3") final int limit
    ) {
        final PageResponse<List<SuggestedCoffeeChatsByMentorResponse>> result = menteeMainSearchUseCase.getSuggestedMentors(
                new GetSuggestedMentors(authenticated.id(), limit)
        );
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "멘토 둘러보기 Endpoint")
    @GetMapping
    public ResponseEntity<SliceResponse<List<MentorSimpleSearchProfile>>> getMentorsByCondition(
            @ModelAttribute @Valid final GetMentorsByConditionRequest request
    ) {
        final SliceResponse<List<MentorSimpleSearchProfile>> result = menteeMainSearchUseCase.getMentorsByCondition(request.toQuery());
        return ResponseEntity.ok(result);
    }
}
