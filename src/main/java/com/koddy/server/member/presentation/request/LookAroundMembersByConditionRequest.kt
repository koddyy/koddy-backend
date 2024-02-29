package com.koddy.server.member.presentation.request

import com.koddy.server.global.utils.FilteringConverter
import com.koddy.server.member.application.usecase.query.LookAroundMenteesByConditionQuery
import com.koddy.server.member.application.usecase.query.LookAroundMentorsByConditionQuery
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class LookAroundMentorsByConditionRequest(
    val languages: String?,

    @field:NotNull(message = "페이지 번호는 필수입니다.")
    @field:Min(value = 1, message = "페이지는 1부터 시작입니다.")
    val page: Int,
) {
    fun toQuery(): LookAroundMentorsByConditionQuery =
        LookAroundMentorsByConditionQuery(
            languages = convertToLanguageCategory(),
            page = page,
        )

    private fun convertToLanguageCategory(): List<Language.Category> {
        if (languages.isNullOrBlank()) {
            return listOf()
        }
        return FilteringConverter.convertToLanguage(languages)
    }
}

data class LookAroundMenteesByConditionRequest(
    val nationalities: String?,

    val languages: String?,

    @field:NotNull(message = "페이지 번호는 필수입니다.")
    @field:Min(value = 1, message = "페이지는 1부터 시작입니다.")
    val page: Int,
) {
    fun toQuery(): LookAroundMenteesByConditionQuery =
        LookAroundMenteesByConditionQuery(
            nationalities = convertToNationality(),
            languages = convertToLanguageCategory(),
            page = page,
        )

    private fun convertToNationality(): List<Nationality> {
        if (nationalities.isNullOrBlank()) {
            return listOf()
        }
        return FilteringConverter.convertToNationality(nationalities)
    }

    private fun convertToLanguageCategory(): List<Language.Category> {
        if (languages.isNullOrBlank()) {
            return listOf()
        }
        return FilteringConverter.convertToLanguage(languages)
    }
}
