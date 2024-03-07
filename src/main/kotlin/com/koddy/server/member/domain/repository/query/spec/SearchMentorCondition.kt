package com.koddy.server.member.domain.repository.query.spec

import com.koddy.server.member.domain.model.Language

data class SearchMentorCondition(
    val language: LanguageCondition,
) {
    data class LanguageCondition(
        val exists: Boolean,
        val values: List<Language.Category>,
    )

    companion object {
        fun basic(): SearchMentorCondition {
            return SearchMentorCondition(LanguageCondition(exists = false, values = emptyList()))
        }

        fun of(languages: List<Language.Category>): SearchMentorCondition {
            return SearchMentorCondition(createLanguageCondition(languages))
        }

        private fun createLanguageCondition(languages: List<Language.Category>): LanguageCondition {
            return when {
                languages.isEmpty() -> LanguageCondition(exists = false, values = emptyList())
                else -> LanguageCondition(exists = true, values = languages)
            }
        }
    }
}
