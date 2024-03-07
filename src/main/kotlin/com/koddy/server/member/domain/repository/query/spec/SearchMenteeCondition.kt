package com.koddy.server.member.domain.repository.query.spec

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality

data class SearchMenteeCondition(
    val nationality: NationalityCondition,
    val language: LanguageCondition,
) {
    data class NationalityCondition(
        val exists: Boolean,
        val values: List<Nationality>,
    )

    data class LanguageCondition(
        val exists: Boolean,
        val values: List<Language.Category>,
    )

    companion object {
        fun basic(): SearchMenteeCondition {
            return SearchMenteeCondition(
                nationality = NationalityCondition(exists = false, values = emptyList()),
                language = LanguageCondition(exists = false, values = emptyList()),
            )
        }

        fun of(
            nationalities: List<Nationality>,
            languages: List<Language.Category>,
        ): SearchMenteeCondition {
            return SearchMenteeCondition(
                nationality = createNationalityCondition(nationalities),
                language = createLanguageCondition(languages),
            )
        }

        private fun createNationalityCondition(nationalities: List<Nationality>): NationalityCondition {
            return when {
                nationalities.isEmpty() -> NationalityCondition(exists = false, values = emptyList())
                else -> NationalityCondition(exists = true, values = nationalities)
            }
        }

        private fun createLanguageCondition(languages: List<Language.Category>): LanguageCondition {
            return when {
                languages.isEmpty() -> LanguageCondition(exists = false, values = emptyList())
                else -> LanguageCondition(exists = true, values = languages)
            }
        }
    }
}
