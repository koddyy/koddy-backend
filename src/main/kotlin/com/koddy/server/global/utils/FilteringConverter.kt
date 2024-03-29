package com.koddy.server.global.utils

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality

object FilteringConverter {
    private const val DELIMITER = ","

    fun convertToNationality(value: String): List<Nationality> {
        return splitValue(value)
            .map { Nationality.from(it) }
    }

    fun convertToLanguage(value: String): List<Language.Category> {
        return splitValue(value)
            .map { Language.Category.from(it) }
    }

    private fun splitValue(value: String): List<String> = value.split(DELIMITER.toRegex())
}
