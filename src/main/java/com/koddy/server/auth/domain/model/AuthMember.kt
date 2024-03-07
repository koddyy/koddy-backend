package com.koddy.server.auth.domain.model

import com.koddy.server.member.domain.model.Member

data class AuthMember(
    val id: Long,
    val name: String,
    val token: AuthToken,
) {
    constructor(
        member: Member<*>,
        token: AuthToken,
    ) : this(member.id, member.name, token)
}
