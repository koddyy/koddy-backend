package com.koddy.server.member.application.usecase.query.response;

import com.koddy.server.member.domain.model.Language;

import java.util.List;

public record LanguageResponse(
        String mainLanguage,
        List<String> subLanguages
) {
    public LanguageResponse(final List<Language> languages) {
        this(getMainLanguage(languages), getSubLanguages(languages));
    }

    private static String getMainLanguage(final List<Language> languages) {
        return languages.stream()
                .filter(it -> it.getType() == Language.Type.MAIN)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getCategory()
                .getValue();
    }

    private static List<String> getSubLanguages(final List<Language> languages) {
        return languages.stream()
                .filter(it -> it.getType() == Language.Type.SUB)
                .map(it -> it.getCategory().getValue())
                .toList();
    }
}
