package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatStatus {
    // MenteeFlow
    MENTEE_APPLY("APPLY"),
    MENTEE_CANCEL("CANCEL"),
    MENTOR_REJECT("REJECT"),
    MENTOR_APPROVE("APPROVE"),
    MENTEE_APPLY_COFFEE_CHAT_COMPLETE("COMPLETE"),

    // MentorFlow
    MENTOR_SUGGEST("SUGGEST"),
    MENTOR_CANCEL("CANCEL"),
    MENTEE_REJECT("REJECT"),
    MENTEE_PENDING("PENDING"),
    MENTOR_FINALLY_REJECT("REJECT"),
    MENTOR_FINALLY_APPROVE("APPROVE"),
    MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE("COMPLETE"),
    ;

    private final String value;

    public static CoffeeChatStatus fromMenteeFlow(final String value) {
        return Stream.of(MENTEE_APPLY, MENTEE_CANCEL, MENTOR_REJECT, MENTOR_APPROVE, MENTEE_APPLY_COFFEE_CHAT_COMPLETE)
                .filter(it -> it.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CoffeeChatException(INVALID_COFFEECHAT_STATUS));
    }

    public static CoffeeChatStatus fromMentorFlow(final String value) {
        return Stream.of(MENTOR_SUGGEST, MENTOR_CANCEL, MENTEE_REJECT, MENTEE_PENDING, MENTOR_FINALLY_REJECT, MENTOR_FINALLY_APPROVE, MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE)
                .filter(it -> it.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CoffeeChatException(INVALID_COFFEECHAT_STATUS));
    }
}
