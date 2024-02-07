package com.koddy.server.global.utils;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_REJECT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class FilteringConverter {
    private static final String DELIMITER = ",";

    public static List<Nationality> convertToNationality(final String value) {
        return Arrays.stream(splitValue(value))
                .map(Nationality::from)
                .toList();
    }

    public static List<Language.Category> convertToLanguage(final String value) {
        return Arrays.stream(splitValue(value))
                .map(Language.Category::from)
                .toList();
    }

    public static List<CoffeeChatStatus> convertToMenteeFlowCoffeeChatStatus(final String value) {
        return Arrays.stream(splitValue(value))
                .map(CoffeeChatStatus::fromMenteeFlow)
                .toList();
    }

    public static List<CoffeeChatStatus> convertToMentorFlowCoffeeChatStatus(final String value) {
        final List<String> splits = Arrays.stream(splitValue(value)).toList();

        final Set<CoffeeChatStatus> result = splits.stream()
                .map(CoffeeChatStatus::fromMentorFlow)
                .collect(Collectors.toSet());

        if (splits.contains(MENTEE_REJECT.getValue())) {
            result.addAll(List.of(MENTEE_REJECT, MENTOR_FINALLY_REJECT));
        }
        return result.stream().toList();
    }

    private static String[] splitValue(final String value) {
        return value.split(DELIMITER);
    }
}
