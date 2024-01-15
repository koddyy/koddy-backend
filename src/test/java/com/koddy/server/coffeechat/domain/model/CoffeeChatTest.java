package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.common.fixture.StrategyFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> 도메인 Aggregate [CoffeeChat] 테스트")
class CoffeeChatTest extends ParallelTest {
    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);
    private final String applyReason = "신청 이유...";

    @Nested
    @DisplayName("CoffeeChat 초기 생성")
    class Construct {
        @Test
        @DisplayName("멘티 -> 멘토에게 커피챗을 신청한다")
        void applyCoffeeChat() {
            // when
            final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
            final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
            final CoffeeChat coffeeChat = CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPLY),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("멘토 -> 멘티에게 커피챗을 제안한다")
        void suggestCoffeeChat() {
            // when
            final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPLY),
                    () -> assertThat(coffeeChat.getStart()).isNull(),
                    () -> assertThat(coffeeChat.getEnd()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("커피챗 신청/제안에 대한 수락")
    class Approve {
        @Test
        @DisplayName("멘티의 커피챗 신청을 수락한다")
        void approveMenteeApply() {
            // given
            final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
            final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
            final CoffeeChat coffeeChat = CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end);

            // when
            final Strategy strategy = StrategyFixture.ZOOM_LINK.toDomain();
            coffeeChat.approveMenteeApply(strategy);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPROVE),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy().getType()).isEqualTo(strategy.getType()),
                    () -> assertThat(coffeeChat.getStrategy().getValue()).isEqualTo(strategy.getValue())
            );
        }

        @Test
        @DisplayName("멘토의 커피챗 제안을 수락한다")
        void approveMentorSuggest() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason);

            // when
            final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
            final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
            coffeeChat.approveMentorSuggest(start, end);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(PENDING),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("커피챗 신청/제안에 대한 거절")
    class Reject {
        private static final String REJECT_REASON = "거절..";

        @Test
        @DisplayName("멘티의 커피챗 신청을 거절한다")
        void rejectMenteeApply() {
            // given
            final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
            final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
            final CoffeeChat coffeeChat = CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end);

            // when
            coffeeChat.reject(REJECT_REASON);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getRejectReason()).isEqualTo(REJECT_REASON),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(REJECT),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("멘토의 커피챗 제안을 거절한다")
        void rejectMentorSuggest() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason);

            // when
            coffeeChat.reject(REJECT_REASON);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getRejectReason()).isEqualTo(REJECT_REASON),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(REJECT),
                    () -> assertThat(coffeeChat.getStart()).isNull(),
                    () -> assertThat(coffeeChat.getEnd()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }
}
