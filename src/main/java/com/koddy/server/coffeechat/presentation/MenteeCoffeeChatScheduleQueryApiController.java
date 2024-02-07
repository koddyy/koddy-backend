package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.GetMenteeCoffeeChatScheduleUseCase;
import com.koddy.server.coffeechat.application.usecase.query.GetMenteeCoffeeChats;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.presentation.dto.request.GetAppliedCoffeeChatScheduleRequest;
import com.koddy.server.coffeechat.presentation.dto.request.GetSuggestedCoffeeChatScheduleRequest;
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

import static com.koddy.server.member.domain.model.Role.MENTEE;

@Tag(name = "4-5-1. 멘티 내 일정 커피챗 상태별 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats/mentees/me")
public class MenteeCoffeeChatScheduleQueryApiController {
    private final GetMenteeCoffeeChatScheduleUseCase getMenteeCoffeeChatScheduleUseCase;

    @Operation(summary = "신청한 커피챗 상태별 조회 Endpoint")
    @GetMapping("/applied")
    @AccessControl(role = MENTEE)
    public ResponseEntity<SliceResponse<List<MenteeCoffeeChatScheduleData>>> getAppliedCoffeeChats(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetAppliedCoffeeChatScheduleRequest request
    ) {
        final SliceResponse<List<MenteeCoffeeChatScheduleData>> result = getMenteeCoffeeChatScheduleUseCase.getAppliedCoffeeChats(
                new GetMenteeCoffeeChats(
                        authenticated.id(),
                        request.convertToCoffeeChatStatus(),
                        request.page()
                )
        );
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "제안받은 커피챗 상태별 조회 Endpoint")
    @GetMapping("/suggested")
    @AccessControl(role = MENTEE)
    public ResponseEntity<SliceResponse<List<MenteeCoffeeChatScheduleData>>> getSuggestedCoffeeChats(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetSuggestedCoffeeChatScheduleRequest request
    ) {
        final SliceResponse<List<MenteeCoffeeChatScheduleData>> result = getMenteeCoffeeChatScheduleUseCase.getSuggestedCoffeeChats(
                new GetMenteeCoffeeChats(
                        authenticated.id(),
                        request.convertToCoffeeChatStatus(),
                        request.page()
                )
        );
        return ResponseEntity.ok(result);
    }
}
