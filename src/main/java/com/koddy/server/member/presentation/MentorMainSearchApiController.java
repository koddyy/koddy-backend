package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.PageResponse;
import com.koddy.server.global.ResponseWrapper;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.member.application.usecase.MentorMainSearchUseCase;
import com.koddy.server.member.application.usecase.query.response.MenteeSimpleSearchProfile;
import com.koddy.server.member.presentation.dto.request.GetMenteesByConditionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "2-9. 커피챗 신청한 멘티, 멘티 둘러보기 조회 API (멘토 전용)")
@RestController
@RequestMapping("/api/mentees")
@RequiredArgsConstructor
public class MentorMainSearchApiController {
    private final MentorMainSearchUseCase mentorMainSearchUseCase;

    @GetMapping("/applied-coffeechats")
    @AccessControl(role = MENTOR)
    public ResponseEntity<ResponseWrapper<List<MenteeSimpleSearchProfile>>> getAppliedMentees(
            @Auth final Authenticated authenticated
    ) {
        final List<MenteeSimpleSearchProfile> result = mentorMainSearchUseCase.getAppliedMentees(authenticated.id());
        return ResponseEntity.ok(ResponseWrapper.from(result));
    }

    @GetMapping
    public ResponseEntity<PageResponse<List<MenteeSimpleSearchProfile>>> getMenteesByCondition(
            @ModelAttribute @Valid final GetMenteesByConditionRequest request
    ) {
        final PageResponse<List<MenteeSimpleSearchProfile>> result = mentorMainSearchUseCase.getMenteesByCondition(request.toQuery());
        return ResponseEntity.ok(result);
    }
}