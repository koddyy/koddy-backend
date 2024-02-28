package com.koddy.server.coffeechat.infrastructure.link.zoom.spec

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest
import com.koddy.server.global.utils.TimeUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * topic = 회의 제목
 *
 * startTime = 회의 시작 시간 (UTC)
 *
 * duration = 회의 기간 (Minute)
 *
 * timezone = Timezone = UTC
 */
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ZoomMeetingLinkRequest(
    val topic: String,
    val startTime: ZonedDateTime,
    val duration: Long,
    val timezone: String,
    val settings: Settings,
) : MeetingLinkRequest {
    constructor(
        topic: String,
        start: LocalDateTime,
        end: LocalDateTime,
    ) : this(
        topic,
        ZonedDateTime.of(TimeUtils.kstToUtc(start), ZoneId.of("UTC")),
        TimeUtils.calculateDurationByMinutes(start, end),
        "UTC",
        Settings(),
    )
}

/**
 * autoRecording = local(로컬 레코딩), cloud(클라우드 레코딩), none(레코딩 X)
 *
 * registrantsConfirmationEmail = 등록 시 이메일 확인 여부
 *
 * registrantsEmailNotification = 등록 시 이메일 발송 여부
 *
 * hostVideo = 시작 시 호스트 비디오 켤지 여부
 *
 * participantVideo = 시작 시 참여자 비디오 켤지 여부
 *
 * muteUponEntry = 참여할 때 음소거 여부
 */
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Settings(
    val autoRecording: String,
    val registrantsConfirmationEmail: Boolean,
    val registrantsEmailNotification: Boolean,
    val hostVideo: Boolean,
    val participantVideo: Boolean,
    val muteUponEntry: Boolean,
) {
    constructor() : this(
        autoRecording = "none",
        registrantsConfirmationEmail = true,
        registrantsEmailNotification = true,
        hostVideo = false,
        participantVideo = false,
        muteUponEntry = true,
    )
}
