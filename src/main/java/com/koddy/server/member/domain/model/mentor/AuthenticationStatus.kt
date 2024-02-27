package com.koddy.server.member.domain.model.mentor

enum class AuthenticationStatus(
    val value: String,
) {
    ATTEMPT("시도"),
    SUCCESS("성공"),
    FAILURE("실패"),
}
