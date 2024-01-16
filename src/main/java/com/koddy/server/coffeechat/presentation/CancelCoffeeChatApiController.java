package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.CancelCoffeeChatUseCase;
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.global.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4-3. 신청/제안한 커피챗 취소 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats/cancel/{coffeeChatId}")
public class CancelCoffeeChatApiController {
    private final CancelCoffeeChatUseCase cancelCoffeeChatUseCase;

    @Operation(summary = "신청/제안한 커피챗 취소 Endpoint")
    @DeleteMapping
    public ResponseEntity<Void> cancel(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId
    ) {
        cancelCoffeeChatUseCase.invoke(new CancelCoffeeChatCommand(coffeeChatId));
        return ResponseEntity.noContent().build();
    }
}
