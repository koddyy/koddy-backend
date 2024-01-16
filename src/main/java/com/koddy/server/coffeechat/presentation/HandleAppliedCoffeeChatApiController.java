package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.HandleAppliedCoffeeChatUseCase;
import com.koddy.server.coffeechat.application.usecase.command.ApproveAppliedCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectAppliedCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.presentation.dto.request.ApproveAppliedCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.RejectAppliedCoffeeChatRequest;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "4-4-1. 멘티가 신청한 커피챗 처리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats/applied")
public class HandleAppliedCoffeeChatApiController {
    private final HandleAppliedCoffeeChatUseCase handleAppliedCoffeeChatUseCase;

    @Operation(summary = "멘티가 신청한 커피챗 거절 Endpoint")
    @PatchMapping("/reject/{coffeeChatId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> reject(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final RejectAppliedCoffeeChatRequest request
    ) {
        handleAppliedCoffeeChatUseCase.reject(new RejectAppliedCoffeeChatCommand(coffeeChatId, request.rejectReason()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멘티가 신청한 커피챗 수락 Endpoint")
    @PatchMapping("/approve/{coffeeChatId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> approve(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final ApproveAppliedCoffeeChatRequest request
    ) {
        handleAppliedCoffeeChatUseCase.approve(new ApproveAppliedCoffeeChatCommand(
                coffeeChatId,
                Strategy.Type.from(request.chatType()),
                request.chatValue()
        ));
        return ResponseEntity.noContent().build();
    }
}
