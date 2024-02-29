package com.koddy.server.member.application.usecase.query

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition

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

data class LookAroundMentorsByConditionQuery(
    val languages: List<Language.Category>,
    val page: Int,
) {
    fun toCondition(): SearchMentorCondition {
        if (languages.isEmpty()) {
            return SearchMentorCondition.basic()
        }
        return SearchMentorCondition.of(languages)
    }
}
