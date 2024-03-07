package com.koddy.server.member.domain.model

import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.INVALID_EMAIL_PATTERN
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.regex.Pattern

@Embeddable
data class Email(
    @Column(name = "email", unique = true)
    val value: String,
) {
    init {
        if (pattern.matcher(value).matches().not()) {
            throw MemberException(INVALID_EMAIL_PATTERN)
        }
    }

    companion object {
        private val pattern: Pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    }
}
