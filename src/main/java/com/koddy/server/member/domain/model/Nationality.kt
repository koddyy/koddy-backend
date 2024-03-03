package com.koddy.server.member.domain.model

import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.INVALID_NATIONALITY

enum class Nationality(
    @JvmField val code: String,
    val value: String,
) {
    KOREA("KR", "한국"),
    USA("EN", "미국"),
    JAPAN("CN", "일본"),
    CHINA("JP", "중국"),
    VIETNAM("VN", "베트남"),
    ETC("ETC", "ETC"),
    ;

    companion object {
        fun from(code: String): Nationality {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
                ?: throw MemberException(INVALID_NATIONALITY)
        }
    }
}
