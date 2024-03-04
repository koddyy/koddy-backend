package com.koddy.server.member.domain.repository.query.spec

import com.koddy.server.member.domain.model.Language

data class SearchMentorCondition(
    val language: LanguageCondition,
) {
    data class LanguageCondition(
        val contains: Boolean,
        val values: List<Language.Category>,
    )

    companion object {
        fun basic(): SearchMentorCondition {
            return SearchMentorCondition(LanguageCondition(false, emptyList()))
        }

        fun of(languages: List<Language.Category>): SearchMentorCondition {
            return SearchMentorCondition(createLanguageCondition(languages))
        }

        private fun createLanguageCondition(languages: List<Language.Category>): LanguageCondition {
            return when {
                languages.isEmpty() -> LanguageCondition(false, emptyList())
                else -> LanguageCondition(true, languages)
            }
        }
    }
}
