package com.koddy.server.coffeechat.infrastructure.link.zoom.spec;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ZoomMeetingLinkResponse(
        String id, // 미팅 ID
        String hostEmail, // 호스트 이메일
        String topic, // 회의 주제 (제목)
        String joinUrl, // 회의 참여 URL
        long duration // 회의 시간 (Minute 기준)
) {
}
