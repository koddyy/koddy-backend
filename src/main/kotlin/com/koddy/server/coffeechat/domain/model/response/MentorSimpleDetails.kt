package com.koddy.server.coffeechat.domain.model.response

data class MentorSimpleDetails(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val school: String,
    val major: String,
    val enteredIn: Int,
)
