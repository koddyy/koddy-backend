package com.koddy.server.member.presentation.request

import com.koddy.server.member.domain.model.Language
import jakarta.validation.constraints.NotBlank

data class LanguageRequest(
    @field:NotBlank(message = "메인 언어를 선택해주세요.")
    val main: String,

    val sub: List<String> = emptyList(),
) {
    fun toLanguages(): List<Language> {
        val mainLanguage = Language(Language.Category.from(main), Language.Type.MAIN)
        val subLanguages: List<Language> = sub.map { Language(Language.Category.from(it), Language.Type.SUB) }
        return listOf(mainLanguage) + subLanguages
    }
}
