package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.PageResponse;
import com.koddy.server.global.ResponseWrapper;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.member.application.usecase.MenteeMainSearchUseCase;
import com.koddy.server.member.application.usecase.query.response.MentorSimpleSearchProfile;
import com.koddy.server.member.presentation.dto.request.GetMentorsByConditionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.koddy.server.member.domain.model.Role.MENTEE;

@Tag(name = "2-10. 커피챗 제안한 멘토, 멘토 둘러보기 조회 API (멘티 전용)")
@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MenteeMainSearchApiController {
    private final MenteeMainSearchUseCase menteeMainSearchUseCase;

    @GetMapping("/suggested-coffeechats")
    @AccessControl(role = MENTEE)
    public ResponseEntity<ResponseWrapper<List<MentorSimpleSearchProfile>>> getSuggestedMentors(
            @Auth final Authenticated authenticated
    ) {
        final List<MentorSimpleSearchProfile> result = menteeMainSearchUseCase.getSuggestedMentors(authenticated.id());
        return ResponseEntity.ok(ResponseWrapper.from(result));
    }

    @GetMapping
    public ResponseEntity<PageResponse<List<MentorSimpleSearchProfile>>> getMentorsByCondition(
            @ModelAttribute @Valid final GetMentorsByConditionRequest request
    ) {
        final PageResponse<List<MentorSimpleSearchProfile>> result = menteeMainSearchUseCase.getMentorsByCondition(request.toQuery());
        return ResponseEntity.ok(result);
    }
}
