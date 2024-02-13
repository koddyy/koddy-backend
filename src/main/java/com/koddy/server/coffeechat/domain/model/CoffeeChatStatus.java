package com.koddy.server.coffeechat.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatStatus {
    // MenteeFlow
    MENTEE_APPLY("waiting"),
    MENTEE_CANCEL("passed"),
    MENTOR_REJECT("passed"),
    MENTOR_APPROVE("scheduled"),
    MENTEE_APPLY_COFFEE_CHAT_COMPLETE("passed"),

    // MentorFlow
    MENTOR_SUGGEST("waiting"),
    MENTOR_CANCEL("passed"),
    MENTEE_REJECT("passed"),
    MENTEE_PENDING("waiting"),
    MENTOR_FINALLY_REJECT("passed"),
    MENTOR_FINALLY_APPROVE("scheduled"),
    MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE("passed"),
    ;

    private final String filter;

    public static List<CoffeeChatStatus> from(final String filter) {
        return Arrays.stream(values())
                .filter(it -> it.filter.equals(filter))
                .toList();
    }
}
