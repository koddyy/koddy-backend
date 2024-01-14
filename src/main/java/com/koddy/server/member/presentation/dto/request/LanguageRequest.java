package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record LanguageRequest(
        @NotBlank(message = "메인 언어를 선택해주세요.")
        String main,

        List<String> sub
) {
    public List<Language> toLanguages() {
        final List<Language> result = new ArrayList<>();
        result.add(new Language(Language.Category.from(this.main), Language.Type.MAIN));
        sub.stream()
                .map(it -> new Language(Language.Category.from(it), Language.Type.SUB))
                .forEach(result::add);
        return result;
    }
}
