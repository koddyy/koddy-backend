package com.koddy.server.member.domain.model

import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.INVALID_LANGUAGE_CATEGORY
import com.koddy.server.member.exception.MemberExceptionCode.INVALID_LANGUAGE_TYPE
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated

@Embeddable
data class Language(
    @Enumerated(STRING)
    @Column(name = "language_category", nullable = false, columnDefinition = "VARCHAR(20)")
    val category: Category,

    @Enumerated(STRING)
    @Column(name = "language_type", nullable = false, columnDefinition = "VARCHAR(20)")
    val type: Type,
) {
    enum class Category(
        val code: String,
        val value: String,
    ) {
        KR("KR", "한국어"),
        EN("EN", "영어"),
        CN("CN", "중국어"),
        JP("JP", "일본어"),
        VN("VN", "베트남어"),
        ;

        companion object {
            fun from(code: String): Category {
                return entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
                    ?: throw MemberException(INVALID_LANGUAGE_CATEGORY)
            }
        }
    }

    enum class Type(
        val value: String,
    ) {
        MAIN("메인 언어"),
        SUB("서브 언어"),
        ;

        companion object {
            fun from(value: String): Type {
                return entries.firstOrNull { it.value == value }
                    ?: throw MemberException(INVALID_LANGUAGE_TYPE)
            }
        }
    }
}
