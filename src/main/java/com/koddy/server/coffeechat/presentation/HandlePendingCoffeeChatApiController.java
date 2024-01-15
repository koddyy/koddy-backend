package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.HandlePendingCoffeeChatUseCase;
import com.koddy.server.coffeechat.application.usecase.command.ApprovePendingCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectPendingCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.presentation.dto.request.ApprovePendingCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.RejectPendingCoffeeChatRequest;
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

@Tag(name = "4-3-3. 최종 결정 대기상태인 커피챗에 대한 멘토의 결정")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats/pending")
public class HandlePendingCoffeeChatApiController {
    private final HandlePendingCoffeeChatUseCase handlePendingCoffeeChatUseCase;

    @Operation(summary = "최종 거절")
    @PatchMapping("/reject/{coffeeChatId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> reject(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final RejectPendingCoffeeChatRequest request
    ) {
        handlePendingCoffeeChatUseCase.reject(new RejectPendingCoffeeChatCommand(coffeeChatId, request.rejectReason()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "최종 수락")
    @PatchMapping("/approve/{coffeeChatId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> approve(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final ApprovePendingCoffeeChatRequest request
    ) {
        handlePendingCoffeeChatUseCase.approve(new ApprovePendingCoffeeChatCommand(
                coffeeChatId,
                Strategy.Type.from(request.chatType()),
                request.chatValue()
        ));
        return ResponseEntity.noContent().build();
    }
}
