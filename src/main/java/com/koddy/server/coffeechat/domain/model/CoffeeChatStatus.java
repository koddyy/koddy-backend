package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatStatus {
    APPLY("신청", "APPLY"),
    SUGGEST("제안", "SUGGEST"),
    CANCEL("취소", "CANCEL"),
    REJECT("거절", "REJECT"),
    PENDING("1차 수락", "PENDING"),
    APPROVE("예정", "APPROVE"),
    COMPLETE("완료", "COMPLETE"),
    NO_SHOW("노쇼", "NO_SHOW"),
    ;

    private final String description;
    private final String value;

    public static CoffeeChatStatus from(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CoffeeChatException(INVALID_COFFEECHAT_STATUS));
    }
}
