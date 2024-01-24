package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.application.usecase.query.GetMentorsByCondition;
import com.koddy.server.member.domain.model.Language;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.CollectionUtils;

import java.util.List;

public record GetMentorsByConditionRequest(
        List<String> languages,

        @NotNull(message = "페이지 번호는 필수입니다.")
        @Min(value = 1, message = "페이지는 1부터 시작입니다.")
        Integer page
) {
    public GetMentorsByCondition toQuery() {
        return new GetMentorsByCondition(
                convertToLanguageCategory(),
                page
        );
    }

    private List<Language.Category> convertToLanguageCategory() {
        if (CollectionUtils.isEmpty(languages)) {
            return List.of();
        }
        return languages.stream()
                .map(Language.Category::from)
                .toList();
    }
}
