package com.koddy.server.member.application.usecase.query;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition;
import org.springframework.util.CollectionUtils;

import java.util.List;

public record GetMenteesByCondition(
        List<Nationality> nationalities,
        List<Language.Category> languages,
        int page
) {
    public SearchMenteeCondition toCondition() {
        if (CollectionUtils.isEmpty(nationalities) && CollectionUtils.isEmpty(languages)) {
            return SearchMenteeCondition.basic();
        }
        return SearchMenteeCondition.of(nationalities, languages);
    }
}
