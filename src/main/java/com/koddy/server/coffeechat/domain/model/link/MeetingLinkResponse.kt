package com.koddy.server.coffeechat.domain.model.link

interface MeetingLinkResponse {
    fun id(): String

    fun hostEmail(): String

    fun topic(): String

    fun joinUrl(): String

    fun duration(): Long
}
