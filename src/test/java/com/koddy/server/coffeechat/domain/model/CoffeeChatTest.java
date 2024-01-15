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
    @DisplayName("멘티의 커피챗 신청에 대한 처리")
    class FromMenteeApply {
        private final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
        private final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));

        @Test
        @DisplayName("거절한다")
        void reject() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end);
            final String rejectReason = "거절..";

            // when
            coffeeChat.rejectFromMenteeApply(rejectReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getRejectReason()).isEqualTo(rejectReason),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(REJECT),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("수락한다")
        void approve() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end);
            final Strategy strategy = StrategyFixture.KAKAO_ID.toDomain();

            // when
            coffeeChat.approveFromMenteeApply(strategy);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPROVE),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isEqualTo(strategy)
            );
        }
    }
}
