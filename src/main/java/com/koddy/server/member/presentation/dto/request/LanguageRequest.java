package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import lombok.Builder;

@Builder
public record LanguageRequest(
        String category,
        String type
) {
    public Language toLanguage() {
        return new Language(Language.Category.from(category), Language.Type.from(type));
    }
}
