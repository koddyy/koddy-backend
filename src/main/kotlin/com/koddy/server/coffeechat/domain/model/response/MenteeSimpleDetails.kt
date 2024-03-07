package com.koddy.server.coffeechat.domain.model.response

data class MenteeSimpleDetails(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val interestSchool: String,
    val interestMajor: String,
)
