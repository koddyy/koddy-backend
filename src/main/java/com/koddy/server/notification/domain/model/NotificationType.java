package com.koddy.server.notification.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    // 멘토가 알림 받는 종류
    MENTEE_APPLY("멘티가 멘토에게 커피챗 신청을 하였다"),
    MENTEE_PENDING("멘토가 제안한 커피챗을 멘티가 1차 수락하였다"),
    MENTEE_REJECT("멘토가 제안한 커피챗을 멘티가 거절하였다"),

    // 멘티가 알림 받는 종류
    MENTOR_SUGGEST("멘토가 멘티에게 커피챗을 제안하였다"),
    MENTOR_APPROVE("멘티가 신청한 커피챗을 멘토가 수락하였다"),
    MENTOR_REJECT("멘티가 신청한 커피챗을 멘토가 거절하였다"),
    MENTOR_PENDING_TO_APPROVE("멘티가 1차 수락한 커피챗을 멘토가 최종 수락하였다"),
    MENTOR_PENDING_TO_REJECT("멘티가 1차 수락한 커피챗을 멘토가 최종 거절하였다"),
    ;

    private final String value;
}
