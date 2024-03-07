package com.koddy.server.common.fixture

import com.koddy.server.member.domain.model.Language

enum class LanguageFixture(
    val category: Language.Category,
    val type: Language.Type,
) {
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

    fun toDomain(): Language {
        return Language(
            category = category,
            type = type,
        )
    }

    companion object {
        fun 메인_한국어_서브_일본어_중국어(): List<Language> {
            return listOf(
                KR_MAIN.toDomain(),
                JP_SUB.toDomain(),
                CN_SUB.toDomain(),
            )
        }

        fun 메인_한국어_서브_영어(): List<Language> {
            return listOf(
                KR_MAIN.toDomain(),
                EN_SUB.toDomain(),
            )
        }

        fun 메인_영어_서브_한국어(): List<Language> {
            return listOf(
                EN_MAIN.toDomain(),
                KR_SUB.toDomain(),
            )
        }

        fun 메인_영어_서브_일본어(): List<Language> {
            return listOf(
                EN_MAIN.toDomain(),
                JP_SUB.toDomain(),
            )
        }
    }
}
