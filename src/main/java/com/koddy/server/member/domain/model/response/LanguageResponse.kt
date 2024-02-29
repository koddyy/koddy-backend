package com.koddy.server.member.domain.model.response

import com.koddy.server.member.domain.model.Language

data class LanguageResponse(
    val main: String,
    val sub: List<String> = emptyList(),
) {
    companion object {
        fun of(languages: List<Language>): LanguageResponse = LanguageResponse(
            main = getMainLanguage(languages),
            sub = getSubLanguages(languages),
        )

        private fun getMainLanguage(languages: List<Language>): String =
            languages
                .first { it.type == Language.Type.MAIN }
                .category
                .code

        private fun getSubLanguages(languages: List<Language>): List<String> =
            languages
                .filter { it.type == Language.Type.SUB }
                .map { it.category.code }
                .toList()
    }
}
