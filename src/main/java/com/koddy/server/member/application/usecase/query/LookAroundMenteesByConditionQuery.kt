package com.koddy.server.member.application.usecase.query

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition

data class LookAroundMenteesByConditionQuery(
    val nationalities: List<Nationality>,
    val languages: List<Language.Category>,
    val page: Int,
) {
    fun toCondition(): SearchMenteeCondition {
        if (nationalities.isEmpty() && languages.isEmpty()) {
            return SearchMenteeCondition.basic()
        }
        return SearchMenteeCondition.of(nationalities, languages)
    }
}
