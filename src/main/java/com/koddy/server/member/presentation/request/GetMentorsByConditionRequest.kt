package com.koddy.server.member.presentation.request

import com.koddy.server.global.utils.FilteringConverter
import com.koddy.server.member.application.usecase.query.GetMentorsByCondition
import com.koddy.server.member.domain.model.Language
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class GetMentorsByConditionRequest(
    val languages: String?,

    @field:NotNull(message = "페이지 번호는 필수입니다.")
    @field:Min(value = 1, message = "페이지는 1부터 시작입니다.")
    val page: Int,
) {
    fun toQuery(): GetMentorsByCondition {
        return GetMentorsByCondition(
            convertToLanguageCategory(),
            page,
        )
    }

    private fun convertToLanguageCategory(): List<Language.Category> {
        if (languages.isNullOrBlank()) {
            return listOf()
        }
        return FilteringConverter.convertToLanguage(languages)
    }
}
