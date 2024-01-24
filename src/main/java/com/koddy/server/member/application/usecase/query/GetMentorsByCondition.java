package com.koddy.server.member.application.usecase.query;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition;
import org.springframework.util.CollectionUtils;

import java.util.List;

public record GetMentorsByCondition(
        List<Language.Category> languages,
        int page
) {
    public SearchMentorCondition toCondition() {
        if (CollectionUtils.isEmpty(languages)) {
            return SearchMentorCondition.basic();
        }
        return SearchMentorCondition.of(languages);
    }
}
