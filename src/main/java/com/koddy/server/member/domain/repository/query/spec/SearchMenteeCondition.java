package com.koddy.server.member.domain.repository.query.spec;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

public record SearchMenteeCondition(
        NationalityCondition nationality,
        LanguageCondition language
) {
    public record NationalityCondition(
            boolean contains,
            List<Nationality> values
    ) {
    }

    public record LanguageCondition(
            boolean contains,
            List<Language.Category> values
    ) {
    }

    public static SearchMenteeCondition basic() {
        return new SearchMenteeCondition(
                createNationalityCondition(Collections.emptyList()),
                createLanguageCondition(Collections.emptyList())
        );
    }

    public static SearchMenteeCondition of(
            final List<Nationality> nationalities,
            final List<Language.Category> languages
    ) {
        return new SearchMenteeCondition(
                createNationalityCondition(nationalities),
                createLanguageCondition(languages)
        );
    }

    private static NationalityCondition createNationalityCondition(final List<Nationality> nationalities) {
        if (CollectionUtils.isEmpty(nationalities)) {
            return new NationalityCondition(false, Collections.emptyList());
        }
        return new NationalityCondition(true, nationalities);
    }

    private static LanguageCondition createLanguageCondition(final List<Language.Category> languages) {
        if (CollectionUtils.isEmpty(languages)) {
            return new LanguageCondition(false, Collections.emptyList());
        }
        return new LanguageCondition(true, languages);
    }
}
