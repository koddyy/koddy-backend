package com.koddy.server.member.domain.model

enum class Role(
    val authority: String,
    val description: String,
) {
    MENTOR("ROLE_MENTOR", "멘토"),
    MENTEE("ROLE_MENTEE", "멘티"),
    ADMIN("ROLE_ADMIN", "관리자"),
    ;

    companion object {
        const val MENTOR_VALUE = "MENTOR"
        const val MENTEE_VALUE = "MENTEE"
    }
}
