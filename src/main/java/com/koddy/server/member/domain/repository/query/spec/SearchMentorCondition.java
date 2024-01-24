package com.koddy.server.member.domain.repository.query.spec;

import com.koddy.server.member.domain.model.Language;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

public record SearchMentorCondition(
        LanguageCondition language
) {
    public record LanguageCondition(
            boolean contains,
            List<Language.Category> values
    ) {
    }

    public static SearchMentorCondition basic() {
        return new SearchMentorCondition(new LanguageCondition(false, Collections.emptyList()));
    }

    public static SearchMentorCondition of(final List<Language.Category> languages) {
        return new SearchMentorCondition(createLanguageCondition(languages));
    }

    private static LanguageCondition createLanguageCondition(final List<Language.Category> languages) {
        if (CollectionUtils.isEmpty(languages)) {
            return new LanguageCondition(false, Collections.emptyList());
        }
        return new LanguageCondition(true, languages);
    }
}
