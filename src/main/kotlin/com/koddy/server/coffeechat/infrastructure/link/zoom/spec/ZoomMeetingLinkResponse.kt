package com.koddy.server.coffeechat.infrastructure.link.zoom.spec

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse

/**
 * id = 미팅 ID
 *
 * hostEmail = 호스트 이메일
 *
 * topic = 회의 주제 (제목)
 *
 * joinUrl = 회의 참여 URL
 *
 * duration = 회의 시간 (Minute 기준)
 */
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ZoomMeetingLinkResponse(
    val id: String,
    val hostEmail: String,
    val topic: String,
    val joinUrl: String,
    val duration: Long,
) : MeetingLinkResponse {
    override fun id(): String = id

    override fun hostEmail(): String = hostEmail

    override fun topic(): String = topic

    override fun joinUrl(): String = joinUrl

    override fun duration(): Long = duration
}
