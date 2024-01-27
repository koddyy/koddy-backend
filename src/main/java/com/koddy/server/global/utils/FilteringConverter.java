package com.koddy.server.global.utils;

import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class FilteringConverter {
    private static final String DELIMITER = ",";

    public static List<Nationality> convertToNationality(final String value) {
        return Arrays.stream(splitValue(value))
                .map(Nationality::from)
                .toList();
    }

    public static List<Language.Category> convertToLanguage(final String value) {
        return Arrays.stream(splitValue(value))
                .map(Language.Category::from)
                .toList();
    }

    private static String[] splitValue(final String value) {
        return value.split(DELIMITER);
    }
}
