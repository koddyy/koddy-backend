package com.koddy.server.coffeechat.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatStatus {
    APPLY("신청"),
    PENDING("멘토 최종 수락 대기"),
    APPROVE("수락"),
    REJECT("거절"),
    COMPLETE("완료"),
    CANCEL("취소"),
    NO_SHOW("노쇼"),
    ;

    private final String value;
}
