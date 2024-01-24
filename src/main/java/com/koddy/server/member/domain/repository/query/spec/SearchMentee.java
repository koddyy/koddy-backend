package com.koddy.server.member.domain.repository.query.spec;

import com.koddy.server.global.exception.GlobalException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.koddy.server.global.exception.GlobalExceptionCode.INVALID_SORT_TYPE;
import static com.koddy.server.member.domain.repository.query.spec.SearchMentee.Sort.LATEST;

public record SearchMentee(
        Sort sort,
        List<String> values
) {
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
        return new SearchMentee(LATEST, Collections.emptyList());
    }

    public static SearchMentee of(final Sort sort, final List<String> values) {
        return new SearchMentee(sort, values);
    }
}
