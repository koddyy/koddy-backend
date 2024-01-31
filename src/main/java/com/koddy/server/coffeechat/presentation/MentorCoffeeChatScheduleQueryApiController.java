package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.GetMentorCoffeeChatScheduleUseCase;
import com.koddy.server.coffeechat.application.usecase.query.GetMentorCoffeeChats;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.presentation.dto.request.GetCoffeeChatScheduleRequest;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.global.query.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "4-5-2. 멘토 내 일정 커피챗 상태별 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats/mentors/me")
public class MentorCoffeeChatScheduleQueryApiController {
    private final GetMentorCoffeeChatScheduleUseCase getMentorCoffeeChatScheduleUseCase;

    @Operation(summary = "제안받은 커피챗 상태별 조회 Endpoint")
    @GetMapping("/suggested")
    @AccessControl(role = MENTOR)
    public ResponseEntity<SliceResponse<List<MentorCoffeeChatScheduleData>>> getAppliedCoffeeChats(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetCoffeeChatScheduleRequest request
    ) {
        final SliceResponse<List<MentorCoffeeChatScheduleData>> result = getMentorCoffeeChatScheduleUseCase.getSuggestedCoffeeChats(
                new GetMentorCoffeeChats(
                        authenticated.id(),
                        request.convertToCoffeeChatStatus(),
                        request.page()
                )
        );
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "신청한 커피챗 상태별 조회 Endpoint")
    @GetMapping("/applied")
    @AccessControl(role = MENTOR)
    public ResponseEntity<SliceResponse<List<MentorCoffeeChatScheduleData>>> getSuggestedCoffeeChats(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetCoffeeChatScheduleRequest request
    ) {
        final SliceResponse<List<MentorCoffeeChatScheduleData>> result = getMentorCoffeeChatScheduleUseCase.getAppliedCoffeeChats(
                new GetMentorCoffeeChats(
                        authenticated.id(),
                        request.convertToCoffeeChatStatus(),
                        request.page()
                )
        );
        return ResponseEntity.ok(result);
    }
}
