package com.koddy.server.coffeechat.presentation.response

data class CreateMeetingLinkResponse(
    val id: String,
    val hostEmail: String,
    val topic: String,
    val joinUrl: String,
    val duration: Long,
)
