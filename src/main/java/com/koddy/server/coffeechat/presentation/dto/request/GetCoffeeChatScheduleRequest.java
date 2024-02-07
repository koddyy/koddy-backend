package com.koddy.server.coffeechat.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GetCoffeeChatScheduleRequest(
        String status,

        @NotNull(message = "페이지 번호는 필수입니다.")
        @Min(value = 1, message = "페이지는 1부터 시작입니다.")
        Integer page
) {
//    public List<CoffeeChatStatus> convertToCoffeeChatStatus() {
//        if (!StringUtils.hasText(status)) {
//            return List.of();
//        }
//        return FilteringConverter.convertToCoffeeChatStatus(status);
//    }
}
