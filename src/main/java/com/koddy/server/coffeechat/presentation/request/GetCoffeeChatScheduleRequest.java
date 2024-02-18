package com.koddy.server.coffeechat.presentation.request;

import com.koddy.server.coffeechat.application.usecase.query.GetMenteeCoffeeChats;
import com.koddy.server.coffeechat.application.usecase.query.GetMentorCoffeeChats;
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.flywaydb.core.internal.util.StringUtils;

import java.util.List;

public record GetCoffeeChatScheduleRequest(
        @NotBlank(message = "필터링 카테고리는 필수입니다.")
        String category,

        String detail,

        @NotNull(message = "페이지 번호는 필수입니다.")
        @Min(value = 1, message = "페이지는 1부터 시작입니다.")
        Integer page
) {
    public GetMentorCoffeeChats toMentorQuery(final long mentorId) {
        return new GetMentorCoffeeChats(
                mentorId,
                convertToCoffeeChatStatus(),
                page
        );
    }

    public GetMenteeCoffeeChats toMenteeQuery(final long menteeId) {
        return new GetMenteeCoffeeChats(
                menteeId,
                convertToCoffeeChatStatus(),
                page
        );
    }

    public List<CoffeeChatStatus> convertToCoffeeChatStatus() {
        if (StringUtils.hasText(detail)) {
            return CoffeeChatStatus.fromCategoryDetail(category, detail);
        }
        return CoffeeChatStatus.fromCategory(category);
    }
}
