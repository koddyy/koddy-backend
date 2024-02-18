package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS;

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
    MENTOR_SUGGEST("suggested"),
    MENTOR_CANCEL("passed"),
    MENTEE_REJECT("passed"),
    MENTEE_PENDING("waiting"),
    MENTOR_FINALLY_REJECT("passed"),
    MENTOR_FINALLY_APPROVE("scheduled"),
    MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE("passed"),
    ;

    private final String filter;

    public static List<CoffeeChatStatus> from(final String filter) {
        if (isAnonymousFilter(filter)) {
            throw new CoffeeChatException(INVALID_COFFEECHAT_STATUS);
        }

        return Arrays.stream(values())
                .filter(it -> it.filter.equals(filter))
                .toList();
    }

    private static boolean isAnonymousFilter(final String filter) {
        return Arrays.stream(values())
                .noneMatch(it -> it.filter.equals(filter));
    }

    public static List<CoffeeChatStatus> withWaitingCategory() {
        return List.of(MENTEE_APPLY, MENTEE_PENDING);
    }

    public static List<CoffeeChatStatus> withSuggstedCategory() {
        return List.of(MENTOR_SUGGEST);
    }

    public static List<CoffeeChatStatus> withScheduledCategory() {
        return List.of(MENTOR_APPROVE, MENTOR_FINALLY_APPROVE);
    }

    public static List<CoffeeChatStatus> withPassedCategory() {
        return List.of(
                MENTEE_CANCEL, MENTOR_REJECT, MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                MENTOR_CANCEL, MENTEE_REJECT, MENTOR_FINALLY_REJECT, MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
        );
    }

    public boolean isMenteeCannotCancel() {
        return Stream.of(MENTEE_APPLY, MENTOR_APPROVE)
                .noneMatch(it -> it == this);
    }

    public boolean isMentorCannotCancel() {
        return Stream.of(MENTOR_SUGGEST, MENTEE_PENDING, MENTOR_FINALLY_APPROVE)
                .noneMatch(it -> it == this);
    }
}
