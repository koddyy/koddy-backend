package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.common.fixture.StrategyFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.COMPLETE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_CANCEL_STATUS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_COMPLETE_STATUS;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> 도메인 Aggregate [CoffeeChat] 테스트")
class CoffeeChatTest extends UnitTest {
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
            final CoffeeChat coffeeChat = CoffeeChat.apply(mentee, mentor, applyReason, start, end);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
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
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, applyReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
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
            final CoffeeChat coffeeChat = CoffeeChat.apply(mentee, mentor, applyReason, start, end);
            final String rejectReason = "거절..";

            // when
            coffeeChat.rejectFromMenteeApply(rejectReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
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
            final CoffeeChat coffeeChat = CoffeeChat.apply(mentee, mentor, applyReason, start, end);
            final Strategy strategy = StrategyFixture.KAKAO_ID.toDomain();

            // when
            coffeeChat.approveFromMenteeApply(strategy);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPROVE),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isEqualTo(strategy)
            );
        }
    }

    @Nested
    @DisplayName("멘토의 커피챗 제안에 대한 처리")
    class FromMentorSuggest {
        @Test
        @DisplayName("거절한다")
        void reject() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, applyReason);
            final String rejectReason = "거절..";

            // when
            coffeeChat.rejectFromMentorSuggest(rejectReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getRejectReason()).isEqualTo(rejectReason),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(REJECT),
                    () -> assertThat(coffeeChat.getStart()).isNull(),
                    () -> assertThat(coffeeChat.getEnd()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("1차 수락한다 (멘토 최종 수락 대기)")
        void pending() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, applyReason);
            final String question = "질문..";
            final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
            final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));

            // when
            coffeeChat.pendingFromMentorSuggest(question, start, end);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isEqualTo(question),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(PENDING),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("최종 결정 대기 상태인 CoffeeChat에 대한 멘토의 결정")
    class PendingCoffeeChat {
        private final String question = "질문..";
        private final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
        private final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));

        private CoffeeChat coffeeChat;

        @BeforeEach
        void setUp() {
            coffeeChat = CoffeeChat.suggest(mentor, mentee, applyReason);
            coffeeChat.pendingFromMentorSuggest(question, start, end);
        }

        @Test
        @DisplayName("거절한다")
        void reject() {
            // given
            final String rejectReason = "거절...";

            // when
            coffeeChat.rejectPendingCoffeeChat(rejectReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isEqualTo(question),
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
            final Strategy strategy = StrategyFixture.KAKAO_ID.toDomain();

            // when
            coffeeChat.approvePendingCoffeeChat(strategy);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isEqualTo(question),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPROVE),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isEqualTo(strategy)
            );
        }
    }

    @Nested
    @DisplayName("신청/제안한 커피챗 취소")
    class Cancel {
        private final String question = "질문..";
        private final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
        private final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));

        @Test
        @DisplayName("APPLY 상태가 아니면 취소가 불가능하다")
        void throwExceptionByCannotCancelStatus() {
            // given
            final CoffeeChat applyCoffeeChat = CoffeeChat.apply(mentee, mentor, applyReason, start, end);
            final CoffeeChat suggestCoffeeChat = CoffeeChat.suggest(mentor, mentee, applyReason);
            applyCoffeeChat.approveFromMenteeApply(StrategyFixture.KAKAO_ID.toDomain());
            suggestCoffeeChat.pendingFromMentorSuggest(question, start, end);

            // when - then
            assertAll(
                    () -> assertThatThrownBy(applyCoffeeChat::cancel)
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(CANNOT_CANCEL_STATUS.getMessage()),
                    () -> assertThatThrownBy(suggestCoffeeChat::cancel)
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(CANNOT_CANCEL_STATUS.getMessage())
            );
        }

        @Test
        @DisplayName("멘티는 자신이 신청한 커피챗을 취소한다")
        void cancelAppliedCoffeeChat() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.apply(mentee, mentor, applyReason, start, end);

            // when
            coffeeChat.cancel();

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("멘토는 자신이 제안한 커피챗을 취소한다")
        void cancelSuggestedCoffeeChat() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, applyReason);

            // when
            coffeeChat.cancel();

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL),
                    () -> assertThat(coffeeChat.getStart()).isNull(),
                    () -> assertThat(coffeeChat.getEnd()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("신청/제안한 커피챗 진행 완료")
    class Complete {
        @Test
        @DisplayName("APPROVE 상태가 아니면 완료 상태로 갱신이 불가능하다")
        void throwExceptionByCannotCompleteStatus() {
            // given
            final CoffeeChat coffeeChatA = MenteeFlow.applyAndReject(월요일_1주차_20_00_시작, mentee, mentor);
            final CoffeeChat coffeeChatB = MentorFlow.suggestAndReject(mentor, mentee);

            // when - then
            assertAll(
                    () -> assertThatThrownBy(coffeeChatA::complete)
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(CANNOT_COMPLETE_STATUS.getMessage()),
                    () -> assertThatThrownBy(coffeeChatB::complete)
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(CANNOT_COMPLETE_STATUS.getMessage())
            );
        }

        @Test
        @DisplayName("APPROVE 상태의 커피챗을 진행한 후 완료 상태로 갱신한다")
        void success() {
            // given
            final CoffeeChat coffeeChat = MenteeFlow.applyAndApprove(월요일_1주차_20_00_시작, mentee, mentor);

            // when
            coffeeChat.complete();

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(COMPLETE),
                    () -> assertThat(coffeeChat.getStart().toLocalDateTime()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getEnd().toLocalDateTime()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isEqualTo(월요일_1주차_20_00_시작.getStrategy())
            );
        }

        @Test
        @DisplayName("멘토는 자신이 제안한 커피챗을 취소한다")
        void cancelSuggestedCoffeeChat() {
            // given
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, applyReason);

            // when
            coffeeChat.cancel();

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL),
                    () -> assertThat(coffeeChat.getStart()).isNull(),
                    () -> assertThat(coffeeChat.getEnd()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Test
    @DisplayName("CoffeeChat의 Reservation[start, end]와 비교 대상 Reservation의 시간대가 겹치는지 확인한다")
    void isReservationIncluded() {
        // given
        final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 2, 18, 0));
        final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 2, 18, 30));
        final CoffeeChat coffeeChat = CoffeeChat.apply(mentee, mentor, applyReason, start, end);

        // when
        final boolean actual1 = coffeeChat.isReservationIncluded(new Reservation(LocalDateTime.of(2024, 2, 1, 18, 20)));
        final boolean actual2 = coffeeChat.isReservationIncluded(new Reservation(LocalDateTime.of(2024, 2, 2, 17, 59)));
        final boolean actual3 = coffeeChat.isReservationIncluded(new Reservation(LocalDateTime.of(2024, 2, 2, 18, 0)));
        final boolean actual4 = coffeeChat.isReservationIncluded(new Reservation(LocalDateTime.of(2024, 2, 2, 18, 20)));
        final boolean actual5 = coffeeChat.isReservationIncluded(new Reservation(LocalDateTime.of(2024, 2, 2, 18, 30)));
        final boolean actual6 = coffeeChat.isReservationIncluded(new Reservation(LocalDateTime.of(2024, 2, 2, 18, 40)));
        final boolean actual7 = coffeeChat.isReservationIncluded(new Reservation(LocalDateTime.of(2024, 2, 3, 18, 20)));

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isFalse(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isTrue(),
                () -> assertThat(actual5).isFalse(),
                () -> assertThat(actual6).isFalse(),
                () -> assertThat(actual7).isFalse()
        );
    }
}
