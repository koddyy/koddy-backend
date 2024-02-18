package com.koddy.server.coffeechat.presentation.request;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.flywaydb.core.internal.util.StringUtils;

import java.util.List;

public record GetCoffeeChatScheduleRequest(
        @NotBlank(message = "커피챗 상태는 필수입니다.")
        String status,

        @NotNull(message = "페이지 번호는 필수입니다.")
        @Min(value = 1, message = "페이지는 1부터 시작입니다.")
        Integer page
) {
    public List<CoffeeChatStatus> convertToCoffeeChatStatus() {
        if (!StringUtils.hasText(status)) {
            return List.of();
        }
        return CoffeeChatStatus.from(status);
    }
}
