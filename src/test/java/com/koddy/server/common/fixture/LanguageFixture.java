package com.koddy.server.common.fixture;

import com.koddy.server.member.domain.model.Language;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum LanguageFixture {
    KR_MAIN(Language.Category.KR, Language.Type.MAIN),
    KR_SUB(Language.Category.KR, Language.Type.SUB),

    EN_MAIN(Language.Category.EN, Language.Type.MAIN),
    EN_SUB(Language.Category.EN, Language.Type.SUB),

    CN_MAIN(Language.Category.CN, Language.Type.MAIN),
    CN_SUB(Language.Category.CN, Language.Type.SUB),

    JP_MAIN(Language.Category.JP, Language.Type.MAIN),
    JP_SUB(Language.Category.JP, Language.Type.SUB),

    VN_MAIN(Language.Category.VN, Language.Type.MAIN),
    VN_SUB(Language.Category.VN, Language.Type.SUB),
    ;

    private final Language.Category category;
    private final Language.Type type;

    public Language toLanguage() {
        return new Language(category, type);
    }

    public static List<Language> 메인_한국어_서브_일본어_중국어() {
        return List.of(
                KR_MAIN.toLanguage(),
                JP_SUB.toLanguage(),
                CN_SUB.toLanguage()
        );
    }

    public static List<Language> 메인_한국어_서브_영어() {
        return List.of(
                KR_MAIN.toLanguage(),
                EN_SUB.toLanguage()
        );
    }

    public static List<Language> 메인_영어_서브_한국어() {
        return List.of(
                EN_MAIN.toLanguage(),
                KR_SUB.toLanguage()
        );
    }

    public static List<Language> 메인_영어_서브_일본어() {
        return List.of(
                EN_MAIN.toLanguage(),
                JP_SUB.toLanguage()
        );
    }
}
