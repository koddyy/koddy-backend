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
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST;
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
                () -> assertThat(CoffeeChatStatus.fromCategory("waiting")).containsExactlyInAnyOrder(MENTEE_APPLY, MENTEE_PENDING)
        );
    }

    @Test
    @DisplayName("`제안 상태` 카테고리인 CoffeeChatStatus를 가져온다")
    void withSuggstedCategory() {
        assertAll(
                () -> assertThat(CoffeeChatStatus.withSuggstedCategory()).containsExactlyInAnyOrder(MENTOR_SUGGEST),
                () -> assertThat(CoffeeChatStatus.fromCategory("suggested")).containsExactlyInAnyOrder(MENTOR_SUGGEST)
        );
    }

    @Test
    @DisplayName("`예정 상태` 카테고리인 CoffeeChatStatus를 가져온다")
    void withScheduledCategory() {
        assertAll(
                () -> assertThat(CoffeeChatStatus.withScheduledCategory()).containsExactlyInAnyOrder(MENTOR_APPROVE, MENTOR_FINALLY_APPROVE),
                () -> assertThat(CoffeeChatStatus.fromCategory("scheduled")).containsExactlyInAnyOrder(MENTOR_APPROVE, MENTOR_FINALLY_APPROVE)
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
                () -> assertThat(CoffeeChatStatus.fromCategory("passed")).containsExactlyInAnyOrder(
                        MENTEE_CANCEL, MENTOR_REJECT, MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                        MENTOR_CANCEL, MENTEE_REJECT, MENTOR_FINALLY_REJECT, MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
                )
        );
    }

    @Test
    @DisplayName("카테고리 + 상세 필터를 통해서 조회하려는 CoffeeChatStatus를 가져온다 [대기 & 지나간 일정]")
    void fromCategoryDetail() {
        assertAll(
                () -> assertThat(CoffeeChatStatus.fromCategoryDetail("waiting", "apply")).containsExactlyInAnyOrder(MENTEE_APPLY),
                () -> assertThat(CoffeeChatStatus.fromCategoryDetail("waiting", "pending")).containsExactlyInAnyOrder(MENTEE_PENDING),

                () -> assertThat(CoffeeChatStatus.fromCategoryDetail("scheduled", "approve")).containsExactlyInAnyOrder(
                        MENTOR_APPROVE,
                        MENTOR_FINALLY_APPROVE
                ),

                () -> assertThat(CoffeeChatStatus.fromCategoryDetail("passed", "cancel")).containsExactlyInAnyOrder(
                        MENTEE_CANCEL,
                        MENTOR_CANCEL
                ),
                () -> assertThat(CoffeeChatStatus.fromCategoryDetail("passed", "reject")).containsExactlyInAnyOrder(
                        MENTOR_REJECT,
                        MENTEE_REJECT,
                        MENTOR_FINALLY_REJECT
                ),
                () -> assertThat(CoffeeChatStatus.fromCategoryDetail("passed", "complete")).containsExactlyInAnyOrder(
                        MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                        MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
                )
        );
    }
}
