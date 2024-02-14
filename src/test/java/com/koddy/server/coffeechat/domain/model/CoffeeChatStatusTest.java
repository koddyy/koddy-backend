package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> 도메인 [CoffeeChatStatus] 테스트")
class CoffeeChatStatusTest extends UnitTest {
    @Test
    @DisplayName("`대기 상태` 카테고리인 CoffeeChatStatus를 가져온다")
    void withWaitingCategory() {
        assertAll(
                () -> assertThat(CoffeeChatStatus.withWaitingCategory()).containsExactlyInAnyOrder(MENTEE_APPLY, MENTEE_PENDING),
                () -> assertThat(CoffeeChatStatus.from("waiting")).containsExactlyInAnyOrder(MENTEE_APPLY, MENTEE_PENDING)
        );
    }

    @Test
    @DisplayName("`제안 상태` 카테고리인 CoffeeChatStatus를 가져온다")
    void withScheduledCategory() {
        assertAll(
                () -> assertThat(CoffeeChatStatus.withScheduledCategory()).containsExactlyInAnyOrder(MENTOR_APPROVE, MENTOR_FINALLY_APPROVE),
                () -> assertThat(CoffeeChatStatus.from("scheduled")).containsExactlyInAnyOrder(MENTOR_APPROVE, MENTOR_FINALLY_APPROVE)
        );
    }

    @Test
    @DisplayName("`지나간 상태` 카테고리인 CoffeeChatStatus를 가져온다")
    void withPassedCategory() {
        assertAll(
                () -> assertThat(CoffeeChatStatus.withPassedCategory()).containsExactlyInAnyOrder(
                        MENTEE_CANCEL, MENTOR_REJECT, MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                        MENTOR_CANCEL, MENTEE_REJECT, MENTOR_FINALLY_REJECT, MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
                ),
                () -> assertThat(CoffeeChatStatus.from("passed")).containsExactlyInAnyOrder(
                        MENTEE_CANCEL, MENTOR_REJECT, MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                        MENTOR_CANCEL, MENTEE_REJECT, MENTOR_FINALLY_REJECT, MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
                )
        );
    }
}
