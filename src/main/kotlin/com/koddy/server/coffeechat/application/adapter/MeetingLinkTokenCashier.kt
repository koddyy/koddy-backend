package com.koddy.server.coffeechat.application.adapter

import java.time.Duration

interface MeetingLinkTokenCashier {
    fun storeViaPlatformId(
        platformId: Long,
        oAuthAccessToken: String,
        duration: Duration,
    )

    fun getViaPlatformId(platformId: Long): String

    fun containsViaPlatformId(platformId: Long): Boolean

    fun storeViaMeetingId(
        meetingId: String,
        oAuthAccessToken: String,
        duration: Duration,
    )

    fun getViaMeetingId(meetingId: String): String

    fun containsViaMeetingId(meetingId: String): Boolean

    fun deleteViaMeetingId(meetingId: String)
}
