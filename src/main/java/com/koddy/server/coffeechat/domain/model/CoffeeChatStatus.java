package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatStatus {
    APPLY("신청", "apply"),
    CANCEL("취소", "cancel"),
    REJECT("거절", "reject"),
    PENDING("1차 수락", "pending"),
    APPROVE("예정", "approve"),
    COMPLETE("완료", "complete"),
    NO_SHOW("노쇼", "noshow"),
    ;

    private final String description;
    private final String value;

    public static CoffeeChatStatus from(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new CoffeeChatException(INVALID_COFFEECHAT_STATUS));
    }
}
