package com.koddy.server.member.domain.repository.query.spec

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality

data class SearchMenteeCondition(
    val nationality: NationalityCondition,
    val language: LanguageCondition,
) {
    data class NationalityCondition(
        val contains: Boolean,
        val values: List<Nationality>,
    )

    data class LanguageCondition(
        val contains: Boolean,
        val values: List<Language.Category>,
    )

    companion object {
        fun basic(): SearchMenteeCondition {
            return SearchMenteeCondition(
                NationalityCondition(false, emptyList()),
                LanguageCondition(false, emptyList()),
            )
        }

        fun of(
            nationalities: List<Nationality>,
            languages: List<Language.Category>,
        ): SearchMenteeCondition {
            return SearchMenteeCondition(
                createNationalityCondition(nationalities),
                createLanguageCondition(languages),
            )
        }

        private fun createNationalityCondition(nationalities: List<Nationality>): NationalityCondition {
            return when {
                nationalities.isEmpty() -> NationalityCondition(false, emptyList())
                else -> NationalityCondition(true, nationalities)
            }
        }

        private fun createLanguageCondition(languages: List<Language.Category>): LanguageCondition {
            return when {
                languages.isEmpty() -> LanguageCondition(false, emptyList())
                else -> LanguageCondition(true, languages)
            }
        }
    }
}
