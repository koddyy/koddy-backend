package com.koddy.server.coffeechat.infrastructure.link.zoom.spec;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.koddy.server.global.utils.DateTimeUtils;

import java.time.ZonedDateTime;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ZoomMeetingLinkRequest(
        String topic, // 회의 제목
        ZonedDateTime startTime, // 회의 시작 시간
        long duration, // 회의 시간 (Minute 기준)
        String timezone, // Timezone -> UTC
        Settings settings
) {
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Settings(
            String autoRecording, // local, cloud, none
            boolean registrantsConfirmationEmail, // 등록 시 이메일 확인 여부
            boolean registrantsEmailNotification, // 등록 시 이메일 발송 여부
            boolean hostVideo, // 시작 시 호스트 비디오 켤지 여부
            boolean participantVideo, // 시작 시 참여자 비디오 켤지 여부
            boolean muteUponEntry // 참여할 때 음소거 여부
    ) {
        public Settings() {
            this("none", true, true, false, false, true);
        }
    }

    public ZoomMeetingLinkRequest(
            final String topic,
            final ZonedDateTime start, // UTC
            final ZonedDateTime end // UTC
    ) {
        this(topic, start, DateTimeUtils.calculateDurationByMinutes(start, end), "UTC", new Settings());
    }
}
