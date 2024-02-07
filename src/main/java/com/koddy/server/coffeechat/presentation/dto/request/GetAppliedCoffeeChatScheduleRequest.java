package com.koddy.server.coffeechat.presentation.dto.request;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.global.utils.FilteringConverter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

import java.util.List;

public record GetAppliedCoffeeChatScheduleRequest(
        String status,

        @NotNull(message = "페이지 번호는 필수입니다.")
        @Min(value = 1, message = "페이지는 1부터 시작입니다.")
        Integer page
) {
    public List<CoffeeChatStatus> convertToCoffeeChatStatus() {
        if (!StringUtils.hasText(status)) {
            return List.of();
        }
        return FilteringConverter.convertToMenteeFlowCoffeeChatStatus(status);
    }
}
