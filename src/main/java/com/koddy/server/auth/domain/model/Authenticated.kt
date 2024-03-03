package com.koddy.server.auth.domain.model

import com.koddy.server.member.domain.model.Role

data class Authenticated(
    val id: Long,
    val authority: String,
) {
    val isMentor: Boolean
        get() = Role.MENTOR.authority == authority

    val isMentee: Boolean
        get() = Role.MENTEE.authority == authority
}
