package com.koddy.server.member.application.usecase.query

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition

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
