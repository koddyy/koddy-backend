package com.koddy.server.coffeechat.presentation.response;

public record CreateMeetingLinkResponse(
        String id, // 미팅 ID
        String hostEmail, // 호스트 이메일
        String topic, // 회의 주제 (제목)
        String joinUrl, // 회의 참여 URL
        long duration // 회의 시간 (Minute 기준)
) {
}
