package com.koddy.server.member.domain.repository.query.spec;

import com.koddy.server.global.exception.GlobalException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.koddy.server.global.exception.GlobalExceptionCode.INVALID_SORT_TYPE;

public record SearchMentee(
        LatestCondition latest,
        NationalityCondition nationality,
        LanguageCondition language
) {
    public record LatestCondition(
            boolean contains
    ) {
    }

    public record NationalityCondition(
            boolean contains,
            List<String> values
    ) {
    }

    public record LanguageCondition(
            boolean contains,
            List<String> values
    ) {
    }

    @Getter
    @RequiredArgsConstructor
    public enum Sort {
        LATEST("latest"),
        NATIONALITY("nationality"),
        LANGUAGE("language"),
        ;

        private final String value;

        public static Sort from(final String value) {
            return Arrays.stream(values())
                    .filter(it -> it.value.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new GlobalException(INVALID_SORT_TYPE));
        }
    }

    public static SearchMentee basic() {
        return new SearchMentee(
                createLatestCondition(true),
                createNationalityCondition(Collections.emptyList()),
                createLanguageCondition(Collections.emptyList())
        );
    }

    public static SearchMentee of(
            final boolean containsLatest,
            final List<String> nationalities,
            final List<String> languages
    ) {
        return new SearchMentee(
                createLatestCondition(containsLatest),
                createNationalityCondition(nationalities),
                createLanguageCondition(languages)
        );
    }

    private static LatestCondition createLatestCondition(final boolean containsLatest) {
        return new LatestCondition(containsLatest);
    }

    private static NationalityCondition createNationalityCondition(final List<String> nationalities) {
        if (CollectionUtils.isEmpty(nationalities)) {
            return new NationalityCondition(false, Collections.emptyList());
        }
        return new NationalityCondition(true, nationalities);
    }

    private static LanguageCondition createLanguageCondition(final List<String> languages) {
        if (CollectionUtils.isEmpty(languages)) {
            return new LanguageCondition(false, Collections.emptyList());
        }
        return new LanguageCondition(true, languages);
    }
}
