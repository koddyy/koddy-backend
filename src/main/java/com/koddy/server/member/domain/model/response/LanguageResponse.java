package com.koddy.server.member.domain.model.response;

import com.koddy.server.member.domain.model.Language;

import java.util.List;

public record LanguageResponse(
        String main,
        List<String> sub
) {
    public static LanguageResponse of(final List<Language> languages) {
        return new LanguageResponse(getMainLanguage(languages), getSubLanguages(languages));
    }

    private static String getMainLanguage(final List<Language> languages) {
        return languages.stream()
                .filter(it -> it.getType() == Language.Type.MAIN)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getCategory()
                .getCode();
    }

    private static List<String> getSubLanguages(final List<Language> languages) {
        return languages.stream()
                .filter(it -> it.getType() == Language.Type.SUB)
                .map(it -> it.getCategory().getCode())
                .toList();
    }
}
