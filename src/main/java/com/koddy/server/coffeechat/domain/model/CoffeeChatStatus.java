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
    MENTEE_APPLY("waiting", "apply"),
    MENTEE_CANCEL("passed", "cancel"),
    MENTOR_REJECT("passed", "reject"),
    MENTOR_APPROVE("scheduled", "approve"),
    MENTEE_APPLY_COFFEE_CHAT_COMPLETE("passed", "complete"),

    // MentorFlow
    MENTOR_SUGGEST("suggested", ""),
    MENTOR_CANCEL("passed", "cancel"),
    MENTEE_REJECT("passed", "reject"),
    MENTEE_PENDING("waiting", "pending"),
    MENTOR_FINALLY_REJECT("passed", "reject"),
    MENTOR_FINALLY_APPROVE("scheduled", "approve"),
    MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE("passed", "complete"),
    ;

    private final String category;
    private final String detail;

    public static List<CoffeeChatStatus> fromCategory(final String category) {
        if (isAnonymousCategory(category)) {
            throw new CoffeeChatException(INVALID_COFFEECHAT_STATUS);
        }

        return Arrays.stream(values())
                .filter(it -> it.category.equals(category))
                .toList();
    }

    private static boolean isAnonymousCategory(final String category) {
        return Arrays.stream(values())
                .noneMatch(it -> it.category.equals(category));
    }

    public static List<CoffeeChatStatus> fromCategoryDetail(
            final String category,
            final String detail
    ) {
        if (isAnonymousCategoryDetail(category, detail)) {
            throw new CoffeeChatException(INVALID_COFFEECHAT_STATUS);
        }

        return Arrays.stream(values())
                .filter(it -> it.category.equals(category) && it.detail.equals(detail))
                .toList();
    }

    private static boolean isAnonymousCategoryDetail(
            final String category,
            final String detail
    ) {
        return Arrays.stream(values())
                .noneMatch(it -> it.category.equals(category) && it.detail.equals(detail));
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
